package tech.muso.rekoil.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

public interface Atom<T : Any> : RekoilContext.ValueNode<T> {
    /**
     * Set the new value for the Atom asynchronously,
     * invalidating observers instantly, and notifying the RekoilScope upon completion.
     */
    fun setValueAsync(value: suspend () -> T)
}

internal open class AtomImpl<T : Any>(
    coroutineScope: CoroutineScope,
    parentContext: SupervisorRekoilContextImpl,
    default: () -> T
) : ValueNodeImpl<T>(coroutineScope), Atom<T> {

    // get by lazy so that we only have one key per object.
    override val key by lazy { object : RekoilContext.Key<AtomImpl<T>>{} }

    override fun <N : RekoilContext.Node> get(key: RekoilContext.Key<N>): N? =
        getPolymorphicElement(key)

    // recompute the value function
    override fun invalidate() {
//        TODO("recompute with the cached async lambda")
    }

    @Volatile override var value = default()
    set(value) {
        // always set backing property first
        field = value
        // when we update our value, notify the parent.
        printdbg("$this set(value) -> send($value)")
        send(value)
    }

    override fun setValueAsync(value: suspend () -> T) {
        TODO("Not yet implemented")
    }

    init {
        // register the node
        parentContext.register(this)
        value = default()
    }

    override fun toString(): String {
        return "Atom[${this.identifier}](`$value`)"
    }
}
