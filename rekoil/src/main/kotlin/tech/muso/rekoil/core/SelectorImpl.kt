package tech.muso.rekoil.core

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi  // Selector requires the functionality of BroadcastChannel
public interface Selector<R> : RekoilContext.ValueNode<R>, RekoilScope, CoroutineContext {
    /**
     * Set the new value for the Selector.
     * This will recompute the dependencies for this node.
     */
    fun <T : R> changeSelector(selectorScope: suspend SelectorScope.() -> T)
}

// TODO: Transform function for T -> R as an alias for single node selector transformation
//public interface TransformSelector<T, R>: Selector<R>

internal class SelectorImpl<T> (
        coroutineScope: CoroutineScope,
        private val parentContext: SupervisorRekoilContextImpl,
        private var selectorScope: suspend SelectorScope.() -> T
) : Selector<T?>, SelectorRekoilScopeImpl<T?>(coroutineScope),
        CoroutineContext by coroutineScope.coroutineContext {   // TODO: fix cancellation behavior by creating own scope

    // member implementations
    override val key by lazy { object : RekoilContext.Key<SelectorImpl<T>>{} }

    override fun invalidate() {
        // only allow one invalidation at a time. This is thread safe (backed by AtomicBoolean)
        if (isValid == false) return
        isValid = false // reset the flag

        // TODO: should we forcefully invalidate all the children in our scope??
        //  This would require isValid bypass; release of pending jobs; and Atom.invalidate().

        // FIXME: ongoing job should be cancelled if release() is called.
        // run on an async scope so that we can suspend during subsequent get(node) calls
        coroutineScope.async {
            // evaluate the block within the selector
            val result = selectorScope.invoke(
                    RekoilDependencyEvaluatorContext(
                            selectorContext = this@SelectorImpl,
                            parentContext = parentContext,
                            emptyDependencyList = dependencies
                    )
            )
            // update our cached result.
            value = result
        }
    }

    // what nodes does this selector depend on?
    // TODO: remove this or use this before first major version release.
    private val dependencies = mutableListOf<RekoilContext.Key<*>>()

    // start valid, because we will call invalidate() immediately, which resets the flag.
    private var _isValid: AtomicBoolean? = null
    internal var isValid: Boolean? = null
        get() {
            // bypass backing property
            return _isValid?.get()
        }
        set(value) {
            if (_isValid == null) {
                _isValid = value?.let { AtomicBoolean(it) }
            } else {
                _isValid?.set(value
                        ?: throw IllegalStateException(
                                "Cannot reset validity to null once a node has been created."))
            }
            // set backing property
            field = value
        }

    // NOTE: This value is the cached value.
    @Volatile override var value: T? = null // marked volatile for multi-thread value obtaining.
    set(value) { // NOTE: ORDER OF OPERATIONS IS IMPORTANT
        // update the backing field first so that observers getting this value immediately receive
        field = value
        value?.let {
            // set isValid when we push a non null update
            isValid = true
            // when we update our value, notify the parent.
            printdbg("$this set(value) -> send($value)")
            send(it)
        }
    }

    override fun <R : T?> changeSelector(selectorScope: suspend SelectorScope.() -> R) {
        TODO("Finish changeSelector() implementation.")

        // clear dependencies
        dependencies.clear()

        // clear dependencies in the scope of this selector
        _rekoil.resetDependencies(this)

//        this.selectorScope = selectorScope
        // TODO: this should create a new Selector object so that the return value can match.
    }

    init {
        // NOTE: ORDER OF OPERATIONS IS IMPORTANT
        parentContext.register(this)   // register before we invalidate to get our result
        invalidate()                            // invalidate to generate and push to parent
    }

    override fun toString(): String {
        return "Selector[${key.hashCode()}]($value)"
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

// TODO: empty dependency list passed for when/if we can evaluate the dependencies another way.
//   without waiting for the result of the value
internal class RekoilDependencyEvaluatorContext(
        val selectorContext: SelectorImpl<*>,
        val parentContext: SupervisorRekoilContextImpl,
        emptyDependencyList: MutableList<RekoilContext.Key<*>>
) : SelectorScope, RekoilScope by selectorContext {

    override suspend fun <R> get(node: RekoilContext.ValueNode<R>): R {
        // TODO: THROW AN EXCEPTION IF THERE IS A PROBLEM WITH THE GRAPH LOADING ACTUAL DATA
        //   OR HANGING TOO LONG. IN WHICH CASE A DEFAULT NEEDS TO BE SET FOR THAT ATOM.
        return parentContext.getAndRegister(selectorContext.key, node)
//                .also { println("get($node): $it") }
    }

}