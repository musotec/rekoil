package tech.muso.rekoil.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext


@ExperimentalCoroutinesApi  // Selector requires the functionality of BroadcastChannel
public interface Selector<R> : RekoilContext.ValueNode<R>, RekoilScope, CoroutineContext {
    /**
     * Set the new value for the Selector.
     * This will recompute the dependencies for this node.
     */
    fun <T : R> changeSelector(selectorScope: suspend SelectorScope.() -> T)
}
