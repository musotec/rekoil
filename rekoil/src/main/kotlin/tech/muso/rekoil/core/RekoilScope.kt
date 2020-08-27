package tech.muso.rekoil.core

import kotlinx.coroutines.*

/**
 * Defines a scope for a new Rekoil dependency graph.
 *
 * A RekoilScope provides access to the creation methods [atom] and [selector]
 * while also encapsulating the [CoroutineScope] that should control execution.
 *
 * Every RekoilScope contains a [RekoilContext] which contains the [RekoilContext.get]
 * method.
 */
public interface RekoilScope {

    // FIXME: this rekoilContext should probably be private/internal.
    public val rekoilContext: RekoilContext

    /**
     * Generate an Atom within the RekoilScope
     * @param key if null, the default key will be used.
     */
    public fun <R : Any> atom(
            coroutineScope: CoroutineScope = rekoilContext.coroutineScope, // inherit parent scope by default
            key: RekoilContext.Key<Atom<R>>? = null,
            isAsync: Boolean = true,
            cache: Boolean = true,  // Planned expanse
        // fresh/refresh (skip cache/recompute w cache fallback)
            value: () -> R
    ) : Atom<R>

    /**
     * Generate a Selector within the RekoilScope.
     * The selector returns a new RekoilScope for scoping within the Selector only.
     */
    public fun <R> selector(
            coroutineScope: CoroutineScope = rekoilContext.coroutineScope, // inherit parent scope by default
            key: RekoilContext.Key<Selector<R>>? = null,
            value: suspend SelectorScope.() -> R
    ) : Selector<R?>

    /**
     * Generate a Selector within the current RekoilScope, inheriting the RekoilScope passed.
     */
    public fun <R> withScope(
            borrowedRekoilScope: RekoilScope,
            coroutineScope: CoroutineScope = rekoilContext.coroutineScope,
            key: RekoilContext.Key<Selector<R>>? = null,
            value: suspend SelectorScope.() -> R
    ) : Selector<R?>

    /**
     * Release all the nodes within this RekoilScope.
     */
    fun releaseScope()
}

public interface SelectorScope : RekoilScope {
    /**
     * Get the value of the passed node, registering the dependency with the parent scope.
     */
    public suspend fun <R> get(
        node: RekoilContext.ValueNode<R>
    ) : R

    public fun release()

    /**
     * Generate a Selector within the RekoilScope.
     * The selector returns a new RekoilScope for scoping within the Selector only.
     */
//    public fun <R> selector(
//            coroutineScope: CoroutineScope = rekoilContext.coroutineScope, // inherit parent scope by default
//            key: RekoilContext.Key<Selector<R>>? = null,
//            value: suspend SelectorScope.() -> R
//    ) : Selector<R?>

}


/**
 * Creates a new [RekoilScope] and calls the specified suspend block with this scope.
 * The provided scope inherits the [coroutineContext][CoroutineScope.coroutineContext]
 * from the outer scope, following the execution pattern of the [coroutineScope] Contract Builder.
 *
 * If [launch] is true, then the RekoilScope will be launched into a new scope.
 * Then the RekoilScope will require a manual [RekoilScope.releaseScope] call.
 */
public suspend fun rekoilScope(launch: Boolean = false, block: suspend RekoilScope.() -> Unit) {
    // TODO: use and implement contracts for some compile time protection
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
    coroutineScope {
        if (launch) {
            RekoilScope(this).launch(block)
        } else {
            val rekoilScope = RekoilScope(this)
            block.invoke(rekoilScope)
            rekoilScope.releaseScope()
        }
    }
}

// TODO: allow RekoilScope plus operator to rescope two added RecoilContexts.

//public object GlobalRekoil : RekoilScope {
//    /**
//     * Returns [EmptyRekoilContext].
//     */
//    override val rekoilContext: RekoilContext
//        get() = EmptyRekoilContext
//}

//public object Rekoil : RekoilScope {
//    var _rekoilContext: SupervisorRekoilContextImpl = SupervisorRekoilContextImpl(RootRekoilNode)
//
//    override val rekoilContext: RekoilContext
//        get() = _rekoilContext
//}

@Suppress("FunctionName")
public fun RekoilScope(coroutineScope: CoroutineScope): RekoilScope =
    RekoilScopeImpl(coroutineScope) // TODO: potentially rescope coroutine scope to allow for our internal scope cancellation.

@Suppress("FunctionName")
public fun RekoilScope(coroutineDispatcher: CoroutineDispatcher): RekoilScope =
    RekoilScope(CoroutineScope(coroutineDispatcher))

/**
 * Launches a new RekoilScope in the background on the provided CoroutineScope
 *
 * TODO: documentation. THIS API IS VERY SUBJECT TO CHANGE OF FUNCTIONALITY
 */
public fun RekoilScope.launch(
    block: suspend RekoilScope.() -> Unit
): Job {
    // TODO: DO SOMETHING IDK
    return this.rekoilContext.coroutineScope.launch {
        block()
    }
}

public fun RekoilScope.async(
        block: suspend RekoilScope.() -> Unit
): Job {
    return this.rekoilContext.coroutineScope.async {
        block()
    }
}

//public inline fun RekoilScope.(block: T.() -> R): R {
//
//}