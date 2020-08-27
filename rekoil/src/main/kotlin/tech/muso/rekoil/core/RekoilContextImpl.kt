package tech.muso.rekoil.core

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tech.muso.rekoil.RekoilDependencyException
import tech.muso.rekoil.RekoilLazyNodeRegistrationException
import tech.muso.rekoil.core.RekoilContext.Key
import tech.muso.rekoil.core.RekoilContext.Node
import tech.muso.rekoil.core.RekoilContext.ValueNode
import java.io.Serializable
import kotlin.coroutines.*

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
//    public override fun plus(context: RekoilContext): RekoilContext = context
//    public override fun minusKey(key: Key<*>): RekoilContext = this
//    public override fun hashCode(): Int = 0
//    public override fun toString(): String = "EmptyRekoilContext"
//}

internal class RootRekoilNode(
    override val coroutineScope: CoroutineScope
) : Node, Key<RootRekoilNode> {

    private val continuations = mutableListOf<RekoilContinuation>()

    companion object Key : RekoilContext.Key<RootRekoilNode>

    override val key: RekoilContext.Key<RootRekoilNode>
        get() = Key

    override fun invalidate() {
        TODO("Determine if this should not be called and maybe throw IllegalStateException.")
    }

    suspend fun registerContinuation(node: ValueNode<*>?) =
        suspendCoroutine<Any?> { continuation ->
            continuations.add(
                    RekoilContinuation(node, continuation)
            )

//            performAsync { value, exception ->
//                when {
//                    exception != null -> // operation had failed
//                        continuation.resumeWithException(exception)
//                    else -> // succeeded, there is a value
//                        continuation.resume(value as T)
//                }
//            }
        }

    fun runContinuations(node: ValueNode<*>) {
        continuations.forEach {
            it.continuation.resume(null)
            continuations.remove(it)
        }
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

//    public override fun <R> fold(initial: R, operation: (R, Node) -> R): R =
//            operation(left.fold(initial, operation), node)

    private fun contains(node: Node): Boolean =
        get(node.key) == node
}

internal data class RekoilContinuation(val node: ValueNode<*>?, val continuation: Continuation<Any?>)

/**
 * A RekoilContext that manages the connection between Nodes within its context.
 */
internal class SupervisorRekoilContextImpl(
    private val parent: RekoilContext?,
    internal val rootNode: Node
) : RekoilContext, Serializable {

    override val coroutineScope: CoroutineScope
        get() = rootNode.coroutineScope

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
     * TODO: should this be allowed? if you clear dependencies then you need to remake them.
     *   probably want a combination of selectors.
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
     * Called from [SelectorImpl].invalidate]
     */
    @Suppress("SENSELESS_COMPARISON") // nodes can be null in spite of compiler
    suspend fun <R, N : ValueNode<R>> getAndRegister(callerKey: Key<*>, node: N): R {
        // check for race condition between scope evaluation and node instantiation
        if (node == null) {
            println("$this Suspending ==> $callerKey.getAndRegister($node)")
//            suspendCoroutine<R> {
//                (rootNode as RootRekoilNode).registerContinuation()
//                // TODO: determine that this has no problems.
//            }

            (rootNode as RootRekoilNode).registerContinuation(node)

            println("$this Resumed <== $callerKey.getAndRegister($node)")

            return getAndRegister(callerKey, node)

            // suspending the Coroutine _should_ have no problems on its own (possibly?) due to the suspension being
            // continued automatically when the time for next execution is available.
            // then we recursively re-call this function, at which point we hope the node has been created.

            // FIXME: for very large programs this _probably_ has problems. (longer to create variables known to compiler)
            //   we will want to use RootRekoilNode to resume the Continuation(s) and smartly re-evaluate.

            // NOTE: this is only a "problem" because we automatically generate keys by lazy delegate
        }

        if(dependencyList[node.key] == null)
            return parent?.get(node)
                    ?: throw RekoilDependencyException(
                            "Node ($node) is not accessible " +
                            "from within the current rekoil scope!"
                    )

        if(dependencyList[node.key] == null)
            throw RekoilLazyNodeRegistrationException()

        // add the caller, requesting value of `node` as a dependency
        if (!dependencyList[node.key]!!.contains(callerKey)) {  // TODO: another type of exception?
            dependencyList[node.key]!!.add(callerKey)
        }

        // below is the logic to obtain the cached selector value
        // without re-computation if it is still valid and we have it

        // if statement checks first for is SelectorImpl because
        // case is false for all Atoms, which do not have property `valid`.
        if (node is SelectorImpl<*> && node.valid != true) {
            // but when it isn't valid; we create a Deferred for when it is ready.
            val deferred: CompletableDeferred<R> = mutex.withLock { // use lock when accessing deferred cache
                // return existing deferrable if present.
                if (pendingValues.containsKey(node.key)) {
                    @Suppress("UNCHECKED_CAST")
                    pendingValues[node.key] as CompletableDeferred<R>
                } else {
                    // otherwise make new completable and memoize it.
                    CompletableDeferred<R>().also {
                        pendingValues[node.key] = it
                        // any pending values are looked up and emitted
                        // when the RekoilContext subscription receives it
                        Log.v("$callerKey asked for $node (invalid); Made new deferrable - $it")
                    }
                }
            }
            // then await the result
            deferred.await()
            Log.v("$callerKey asked for $node - deferrable returned ${deferred.getCompleted()}")
            return deferred.getCompleted()
        } else {
            // when valid, return from `node.value` directly (well we look up the key)
            return get(node) ?: throw RekoilLazyNodeRegistrationException()
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

        (rootNode as RootRekoilNode).runContinuations(childNode)

        // create a subscription and consume updates
        childNode.subscribe {
            Log.r("$this.update($childNode -> $it)  [${Thread.currentThread().name}]")

            // update values in async context for mutex synchronization against suspend functions.
            coroutineScope.async {
                // so that we can lock against get(node): T and release()
                mutex.withLock {
                    // update the completable deferred for any selectors waiting on the result
                    @Suppress("UNCHECKED_CAST")
                    if (pendingValues[childNode.key] as? CompletableDeferred<T> != null) {
                        Log.v("$childNode !! posting deferred result $it !!")
                        (pendingValues[childNode.key] as? CompletableDeferred<T>)?.complete(it)
                        pendingValues.remove(childNode.key)

                        // do not invalidate currently running coroutines that are
                        // currently deferred (whose result we just returned).
                        return@withLock // return to avoid cancellation via invalidate below
                    }

                    // invalidate all the nodes in this scope
                    // TODO: improve by separating out Selectors? Atoms shouldn't have dependencies.
                    dependencyList[childNode.key]?.forEach { key ->
                        get(key)?.invalidate()
                    }
                }
            }
        }.also {
            Log.r("$this.register() <- $childNode via $it)")
            // when we have released the node, this channel will be closed
            it.invokeOnCompletion {
                // TODO: mark the ValueNode as released and throw IllegalStateException upon access.

                // release dependants on childNode?

                // remove dependency list
                dependencyList.remove(childNode.key)

                // TODO: does a removed node leak? possibly if reference held.
                // release childNode?
                (childNode as? Selector<*>)?.apply {
                    release()
                    releaseScope()
                }
            }
        }
    }

    /*
     * Release all the nodes in the scope to clean up their BroadcastChannels.
     * NOTE: important that this does not be named release()
     *   to avoid conflicting with the Selector.release() which is both a ValueNode
     *   with a channel and contains it's own scope.
     */
    fun releaseScope() {
        // TODO: this should probably be a suspend function and NOT async in a new coroutine.
        coroutineScope.launch {
            mutex.withLock {
                cachedNodes.forEach {
                    (it.value as? ValueNode<*>)?.let { node ->
                        // release child scope of selector.
                        if (node is SelectorImpl<*>) {
                            node.releaseScope()
                        }
                        node.release()
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

    inline fun <T> Selector(
        parent: RekoilContext = this,
        coroutineScope: CoroutineScope = this.coroutineScope,
        noinline selectorScope: suspend SelectorScope.() -> T
    ): Selector<T?> {
        return SelectorImpl<T>(coroutineScope, parent, this, selectorScope)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return "RekoilContext[${Integer.toHexString(rootNode.key.hashCode())}]" +
                (parent?.let { "(parent=$parent)"} ?: "")
    }
}