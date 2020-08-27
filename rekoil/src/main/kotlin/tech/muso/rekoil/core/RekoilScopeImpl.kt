package tech.muso.rekoil.core

import kotlinx.coroutines.CoroutineScope

open class SupervisorScopeImpl(
        val parent: RekoilContext?,
        rootNode: RekoilContext.Node,
        val coroutineScope: CoroutineScope
) : RekoilScope {

    // allow for the nodes within this scope to be watched, so that we can invalidate them
    internal val _rekoil: SupervisorRekoilContextImpl = SupervisorRekoilContextImpl(parent, rootNode)

    // FIXME: this can probably be private/internal.
    override val rekoilContext: RekoilContext
        get() = _rekoil

    override fun <R : Any> atom(
            coroutineScope: CoroutineScope,
            key: RekoilContext.Key<Atom<R>>?,
            isAsync: Boolean,
            cache: Boolean,
            value: () -> R
    ): Atom<R> {
        return _rekoil.Atom(coroutineScope, value)
    }

    override fun <R> selector(
            scope: CoroutineScope,
            key: RekoilContext.Key<Selector<R>>?,
            value: suspend SelectorScope.() -> R
    ): Selector<R?> {
        return _rekoil.Selector(parent ?: _rekoil, scope, value)
    }

    override fun <R> withScope(
            borrowedRekoilScope: RekoilScope,
            coroutineScope: CoroutineScope,
            key: RekoilContext.Key<Selector<R>>?,
            value: suspend SelectorScope.() -> R
    ): Selector<R?> {
        // TODO: FIXME: the passed parent context should be the joined context.
        // create selector that has the passed scope as it's official parent
        return borrowedRekoilScope.selector(coroutineScope, key, value)
                .also {
                    // but also register the node with the context it was created in.
                    _rekoil.register(it)
                }
    }

    override fun releaseScope() {
        _rekoil.releaseScope()
    }
}

/**
 * Create a RekoilScope with a new RootRekoilNode.
 *
 * The root node is used for bookkeeping across all suspended coroutines in the scope.
 */
internal open class RekoilScopeImpl(
        coroutineScope: CoroutineScope
) : SupervisorScopeImpl(null, RootRekoilNode(coroutineScope), coroutineScope)


/**
 * Create a Selector (child) scope, which also extends the [ValueNode] to hold its data.
 */
internal abstract class SelectorRekoilScopeImpl<T>(
    private val parent: RekoilContext,
    private val rootNode: RekoilContext.Node,
    coroutineScope: CoroutineScope
) : ValueNodeImpl<T>(coroutineScope),
        RekoilScope by SupervisorScopeImpl(parent, rootNode, coroutineScope),
        RekoilContext.ValueNode<T> {

//    override fun <R> selector(
//            coroutineScope: CoroutineScope,
//            key: RekoilContext.Key<Selector<R>>?,
//            value: suspend SelectorScope.() -> R
//    ): Selector<R?> {
//        return _rekoil.Selector(parent, coroutineScope, value)
//    }
//
//    override fun <R> withScope(
//            rekoilScope: RekoilScope,
//            coroutineScope: CoroutineScope,
//            key: RekoilContext.Key<Selector<R>>?,
//            value: suspend SelectorScope.() -> R
//    ): Selector<R?> {
//        return _rekoil.Selector(rekoilScope.rekoilContext, coroutineScope, value)
//                .also {
//                    // also register the node with the context it was created in.
//                    _rekoil.register(it)
//                }
//    }

}