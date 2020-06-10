package tech.muso.rekoil.core

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tech.muso.rekoil.core.RekoilContext.Key
import tech.muso.rekoil.core.RekoilContext.Node
import tech.muso.rekoil.core.RekoilContext.ValueNode
import java.io.Serializable

// TODO: use or remove. restriction is probably fine.
//public abstract class AbstractRekoilContextNode(public override val key: Key<*>) : Node

/**
 * Used internally. TODO: create documentation when supporting library extension.
 */
public abstract class AbstractRekoilContextKey<B : Node, N : B>(
    baseKey: Key<B>,
    private val safeCast: (node: Node) -> N?
) : Key<N> {

    private val topmostKey: Key<*> =
        if (baseKey is AbstractRekoilContextKey<*, *>) baseKey.topmostKey else baseKey

    internal fun tryCast(node: Node): N? = safeCast(node)
    internal fun isSubKey(key: Key<*>): Boolean = key === this || topmostKey === key
}

/**
 * Used to re-obtain type from the abstract key so that type inference works at compile time.
 */
public fun <N : Node> Node.getPolymorphicElement(key: Key<N>): N? {
    if (key is AbstractRekoilContextKey<*, *>) {
        @Suppress("UNCHECKED_CAST")
        return if (key.isSubKey(this.key)) key.tryCast(this) as? N else null
    }
    @Suppress("UNCHECKED_CAST")
    return if (this.key === key) this as N else null
}

/*
 * Planned for future.
 */
//public object EmptyRekoilContext : RekoilContext, Serializable {
//    private const val serialVersionUID: Long = 0    // zero until first major release & use
//    override val coroutineScope: CoroutineScope = GlobalScope // generates new empty scope.
////    private fun readResolve(): Any = EmptyRekoilContext   // TODO??
//
//    public override fun <N : Node> get(key: Key<N>): N? = null
//    // TODO: support operator functions, for Empty case, simple returns
////    public override fun plus(context: RekoilContext): RekoilContext = context
////    public override fun minusKey(key: Key<*>): RekoilContext = this
//    public override fun hashCode(): Int = 0
//    public override fun toString(): String = "EmptyRekoilContext"
//}

internal class RootRekoilNode(
    override val coroutineScope: CoroutineScope
) : Node, Key<RootRekoilNode> {

    companion object Key : RekoilContext.Key<RootRekoilNode>

    override val key: RekoilContext.Key<RootRekoilNode>
        get() = Key

    override fun invalidate() {
        TODO("Determine if this should not be called and maybe throw IllegalStateException.")
    }
}

//--------------------- internal impl ---------------------

// do not expose this class

/*
 * Contains internal implementation of a joined RekoilContext
 * with left-biased search; TODO: test left/right addition when implemented.
 *                           This searches the right side first (param node)
 */
internal class CombinedRekoilContextImpl(
    private val left: RekoilContext,
    private val node: Node
) : RekoilContext, Serializable {

    // TODO: handle addition of contexts and determine what scope they get???
    override val coroutineScope: CoroutineScope = left.coroutineScope

    /*
     * Iterate over the context and search for the key.
     */
    override fun <N : Node> get(key: Key<N>): N? {
        var cur = this
        while (true) {
            // if we have the node in this context, return it
            cur.node[key]?.let { return it }
            // otherwise get the context (joined by left)
            val next = cur.left
            // if it is still this wrapper, increase depth
            if (next is CombinedRekoilContextImpl) {
                cur = next
            } else {
                // otherwise return the key from the next context
                return next[key]
            }
        }
    }

    private fun contains(node: Node): Boolean =
        get(node.key) == node
}

/**
 * A RekoilContext that manages the connection between Nodes within its context.
 */
internal class SupervisorRekoilContextImpl(
    private val node: Node
) : RekoilContext, Serializable {

    override val coroutineScope: CoroutineScope
        get() = node.coroutineScope

    // TODO: maybe sort these nodes by most frequently updated??
    private val cachedNodes: MutableMap<Key<*>, Node> = mutableMapOf()
    // TODO: look into performance of lock vs concurrent hashmap vs lockfree/volatile deferred map
    private val pendingValues: MutableMap<Key<*>, CompletableDeferred<*>> = mutableMapOf()
    private val dependencyList: MutableMap<Key<*>, MutableList<Key<*>>> = mutableMapOf()

    /*
     * Mutex for locking critical section on read/write of pendingValues Map
     */
    private val mutex = Mutex()


    /*
     * Clear the list of dependencies for the passed node.
     */
    fun resetDependencies(node: Node) {
        dependencyList[node.key]?.clear()
    }

    /*
     * Return the cached value for the node value if we supervise it. Null otherwise.
     */
    override fun <N : Node> get(key: Key<N>): N? {
        return cachedNodes[key]?.getPolymorphicElement(key)
    }

    /*
     * Get the value and register a dependency.
     */
    suspend fun <R, N : ValueNode<R>> getAndRegister(callerKey: Key<*>, node: N): R {
        // add the caller, requesting value of `node` as a dependency
        if (!dependencyList[node.key]!!.contains(callerKey)) {
            dependencyList[node.key]!!.add(callerKey)
        }

        // get the cached value.
        if (node is SelectorImpl<*> && node.isValid != true) {     // TODO: better way to do this. (isValid)?
            // if the value is null, then attempt

            val deferred: CompletableDeferred<R> = mutex.withLock { // use lock when accessing deferred cache
                // return existing deferrable if present
                if (pendingValues.containsKey(node.key)) {
                    @Suppress("UNCHECKED_CAST")
                    pendingValues[node.key] as CompletableDeferred<R>
                } else {
                    // new completable and cache it.
                    CompletableDeferred<R>().also {
                        pendingValues[node.key] = it
//                            println("dbg - $callerKey asked for $node - MADE NEW cached deferrable $it")
                    }
                }
            }
            // then await the result
            deferred.await()
//            println("dbg - $callerKey asked for $node - deferrable returned ${deferred.getCompleted()}")
            return deferred.getCompleted()
        } else {
            // otherwise return the value from the node.
            return get(node)!!
        }
    }

    /*
     * Register the node with the RekoilContext; caching the node reference.
     */
    @Suppress("DeferredResultUnused")   //
    internal fun <T> register(childNode: ValueNode<T>) {
        // put in the list
        cachedNodes[childNode.key] = childNode

        // create empty list of dependencies on register
        if (dependencyList[childNode.key] == null) {
            dependencyList[childNode.key] = mutableListOf()
        }

        // create a subscription and consume updates
        childNode.subscribe {
            // update values in async context for mutex synchronization against suspend functions.
            coroutineScope.async {
                // so that we can lock against get(node): T and release()
                mutex.withLock {
//                    println("dbg - $childNode sent $it")
                    // update the completable deferred for any selectors waiting on the result
                    @Suppress("UNCHECKED_CAST")
                    if (pendingValues[childNode.key] as? CompletableDeferred<T> != null) {
//                        println("dbg - $childNode posting deferred result $it")
                        (pendingValues[childNode.key] as? CompletableDeferred<T>)?.complete(it)
                        pendingValues.remove(childNode.key)
                    }
                    // invalidate all the nodes in this scope
                    // TODO: improve by separating out Selectors? Atoms shouldn't have dependencies.
                    dependencyList[childNode.key]?.forEach { key ->
                        get(key)?.invalidate()
                    }
                }
            }
        }
    }

    /*
     * Release all the nodes in the scope so long as the
     */
    fun release() {
        // TODO: this should use suspend function NOT async.
        coroutineScope.launch {
            mutex.withLock {
                cachedNodes.forEach {
                    (it.value as? ValueNode<*>)?.let {
                        if (it is SelectorImpl<*>) {
                            // FIXME: this can almost certainly break explicit release()
                            //  but for now, the coroutineScope will always destroy channels
                            if (it.isValid == false) return@let
                        }
                        it.release()
                    }
                }
            }
        }
    }

    fun <T : Any> Atom(
        coroutineScope: CoroutineScope = this.coroutineScope,
        default: () -> T
    ): Atom<T> {
        return AtomImpl<T>(coroutineScope, this, default)
    }

    fun <T> Selector(
        coroutineScope: CoroutineScope = this.coroutineScope,
        selectorScope: suspend SelectorScope.() -> T
    ): Selector<T?> {
        return SelectorImpl<T>(coroutineScope, this, selectorScope)
    }
}