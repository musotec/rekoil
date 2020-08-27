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
    val default: () -> T
) : ValueNodeImpl<T>(coroutineScope), Atom<T> {

    // get by lazy so that we only have one key per object.
    override val key by lazy { object : RekoilContext.Key<AtomImpl<T>>{} }

    override fun <N : RekoilContext.Node> get(key: RekoilContext.Key<N>): N? =
        getPolymorphicElement(key)

    // recompute the value function
    override fun invalidate() {
        value = default()   // TODO: evaluate in async
    }

    @Volatile override var value = default().also {
        Log.a("$this.value <- $it")
    }
    set(value) {
        if (field != value) {
            Log.a("$this.value <- $value")
            // always set backing property first
            field = value
            // otherwise there is a race condition between
            // the scope's broadcast channel subscription [RekoilContext.register()]
            // and the
            send(value) {
                // when we update our value, notify the parent.
                Log.a("$shortName send($value)")
            }
        }
    }

    override fun setValueAsync(value: suspend () -> T) {
        TODO("Not yet implemented")
    }

    init {
        // register the node
        parentContext.register(this)
        value = default()
    }

    private inline val shortName: String get() = "Atom[${this.identifier}]"

    // needed because value can be null prior to invocation of default
    @Suppress("UNNECESSARY_SAFE_CALL", "USELESS_ELVIS")
    override fun toString(): String {
        return shortName + (value?.let { "(`$value`)" } ?: "")
    }
}
