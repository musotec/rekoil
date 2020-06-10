package tech.muso.rekoil.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * NOTE: this class wraps the ExperimentalCoroutinesApi for the BroadcastChannel.
 */
internal abstract class ValueNodeImpl<T>(
    override val coroutineScope: CoroutineScope
) : RekoilContext.ValueNode<T> {

    /*
     * BroadcastChannel for communicating single source of truth to many observers.
     */
    @ExperimentalCoroutinesApi
    private var broadcastChannel: BroadcastChannel<T>? = makeChannel()

    /*
     * Create a channel for sending our data from this node to any subscribers.
     * The channel is created in its own independent coroutine scope.
     */
    @ExperimentalCoroutinesApi
    private fun makeChannel(): BroadcastChannel<T> {
        return ConflatedBroadcastChannel<T>()
    }

    /*
     * Open a new ReceiveChannel for receiving values from the internal BroadcastChannel.
     * This will remake a channel if the previous one was closed (checked by nullity over property).
     */
    @ExperimentalCoroutinesApi
    private fun openSubscription(): ReceiveChannel<T> {
        // check via nullity over property accessors which require method calls.
        // just make sure channel is set to null in release().
        if (broadcastChannel == null) {
            makeChannel()
        }

        return broadcastChannel!!.openSubscription().also {
            // FIXME: this should not invalidate the parent
            // immediately send existing value over to the new subscriber
            if (this is SelectorImpl<*>) {
                if (this.isValid != true) return@also // don't emit if we aren't valid on listen.
            }


            coroutineScope.launch {
                // default case; emit current value for convenience
                printdbg("${this@ValueNodeImpl} openSubscription() -> send($value)")
                send(value)
            }
        }
    }

    /*
     * [ValueNode.subscribe] implementation.
     * It automatically makes the subscription within an async call so that the consumption block
     * does not stop program execution, and operates as an asynchronous callback interface.
     */
    @ExperimentalCoroutinesApi
    override fun subscribe(onValueChanged: (T) -> Unit): Job {
        return coroutineScope.async {
            printdbg("${this@ValueNodeImpl} subscribed")
            var lastReceivedValue: T? = null
            openSubscription().consumeEach {
                if (lastReceivedValue != it) onValueChanged(it)
                lastReceivedValue = it
            }
        }
    }

    /*
     * Blocking send implementation for sending the new values over the BroadcastChannel.
     * TODO: evaluate if this could be async.
     *  Currently, passed value is already computed, so no need to suspend.
     */
    @Suppress("NOTHING_TO_INLINE")
    internal inline fun send(newValue: T) {
        coroutineScope.launch {
            printdbg("$this !! send($value)")
            broadcastChannel?.send(newValue)
        }
    }

    /*
     * Release our BroadcastChannel (independently scoped) manually, stopping all pending receives.
     */
    override fun release() {
        broadcastChannel?.close() // Throwable("Selector $this was released."))
        broadcastChannel = null
        releaseCallback?.invoke(value)  // pass through the last cached value to any callback
    }

    /*
     * Set special release callback for detecting when we have completed sending.
     */
    private var releaseCallback: ((T) -> Unit)? = null
    override fun onRelease(block: (T) -> Unit) {
        releaseCallback = block
    }
}