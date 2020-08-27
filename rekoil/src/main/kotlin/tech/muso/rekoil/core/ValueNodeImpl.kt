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
        return ConflatedBroadcastChannel<T>()   // use conflated BroadcastChannel for memory reasons
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
                if (this.valid != true) {
                    Log.r("${this@ValueNodeImpl} openSubscription()")
                    return@also // don't emit if we aren't valid on listen.
                }
            }

            // default case; emit current value for convenience
            Log.r("${this@ValueNodeImpl} openSubscription() -> send($value)")
            forcedSend(value)
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
            Log.w("${this@ValueNodeImpl} subscribed to <- Job:[$this]")
            openSubscription().consumeEach {
                onValueChanged(it)
            }
        }
    }

    /*
     * Blocking send implementation for sending the new values over the BroadcastChannel.
     * TODO: evaluate if this could be async.
     *  Currently, passed value is already computed, so no need to suspend.
     */
    var senderJob: Job? = null
    @Suppress("NOTHING_TO_INLINE")
    internal inline fun send(newValue: T, crossinline onSendComplete: (value: T) -> Unit) {
        if (senderJob?.isActive == true) {
            Log.v("$this cancelled ongoing send (value updated to $newValue)")
        }
        senderJob?.cancel() // cancel any currently pending sends to prevent flooding broadcast channel
        senderJob = coroutineScope.launch {
            Log.v("$this !! send($value)")
            broadcastChannel?.send(newValue)
            onSendComplete.invoke(newValue)
        }
//        .also {
//            it.invokeOnCompletion {
//                Log.v("$this senderJob.onComplete(error:${it!=null}) -> field = $newValue")
//            }
//        }
    }

    internal inline fun forcedSend(value: T) {
        coroutineScope.launch {
            Log.v("$this !! forcedSend($value)")
            broadcastChannel?.send(value)
        }
    }

    /*
     * Release our BroadcastChannel (independently scoped) manually.
     * This stops all receiver Jobs and invokes the code block in [onRelease],
     * passing through the last value upon being closed.
     */
    override fun release() {
        broadcastChannel?.close()
        broadcastChannel = null
        releaseCallback?.invoke(value)
    }

    /*
     * Set special release callback for detecting when we have completed sending.
     */
    private var releaseCallback: ((T) -> Unit)? = null
    override fun onRelease(block: (T) -> Unit) {
        releaseCallback = block
    }
}