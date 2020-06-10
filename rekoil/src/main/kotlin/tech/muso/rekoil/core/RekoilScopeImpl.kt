package tech.muso.rekoil.core

import kotlinx.coroutines.CoroutineScope

internal class RekoilScopeImpl(
    coroutineScope: CoroutineScope
) : RekoilScope {

    internal val _node: RootRekoilNode = RootRekoilNode(coroutineScope)

    // allow for the nodes within this scope to be watched, so that we can invalidate them
    private val _rekoil: SupervisorRekoilContextImpl = SupervisorRekoilContextImpl(_node)

    override val rekoilContext: RekoilContext
        get() = _rekoil

    override fun <R : Any> atom(
        scope: CoroutineScope,
        key: RekoilContext.Key<Atom<R>>?,
        isAsync: Boolean,
        cache: Boolean,
        value: () -> R
    ): Atom<R> {
        return _rekoil.Atom(scope, value)
    }

    override fun <R> selector(
        scope: CoroutineScope,
        key: RekoilContext.Key<Selector<R>>?,
        value: suspend SelectorScope.() -> R
    ): Selector<R?> {
        return _rekoil.Selector(scope, value)
    }

    override fun release() {
        _rekoil.release()
    }
}


internal abstract class SelectorRekoilScopeImpl<T>(
    coroutineScope: CoroutineScope
) : ValueNodeImpl<T>(coroutineScope), RekoilScope, RekoilContext.ValueNode<T> {

    internal val _node: RekoilContext.Node = RootRekoilNode(coroutineScope)

    // allow for the nodes within this scope to be watched, so that we can invalidate them
    internal val _rekoil: SupervisorRekoilContextImpl = SupervisorRekoilContextImpl(_node)

    override val rekoilContext: RekoilContext
        get() = _rekoil

    override fun <R : Any> atom(
        scope: CoroutineScope,
        key: RekoilContext.Key<Atom<R>>?,
        isAsync: Boolean,
        cache: Boolean,
        value: () -> R
    ): Atom<R> {
        return _rekoil.Atom(scope, value)
    }

    override fun <R> selector(
        scope: CoroutineScope,
        key: RekoilContext.Key<Selector<R>>?,
        value: suspend SelectorScope.() -> R
    ): Selector<R?> {
        return _rekoil.Selector(scope, value)
    }
}