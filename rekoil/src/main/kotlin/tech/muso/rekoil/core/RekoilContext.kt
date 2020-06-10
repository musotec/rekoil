package tech.muso.rekoil.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job

public interface RekoilContext {
    /**
     * The CoroutineScope to execute async calls for nodes within this context.
     */
    public val coroutineScope: CoroutineScope

    /**
     * Returns the node with the given [key] from this context or `null` if not present.
     */
    public operator fun <N : Node> get(key: Key<N>): N?

    /**
     * Returns the cached node value if it is within this context.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <R, N : ValueNode<R>> get(node: N) : R = (get(node.key) as N).value
        ?: throw NoSuchElementException("The node $node was not found within the RekoilContext: $this")

    @ExperimentalCoroutinesApi
    @Suppress("UNCHECKED_CAST")
    // TODO: finish this implementation & probably move this.
    /** @hide */
    private suspend fun <R, N : ValueNode<R>> getActual(node: N) : R {
        val n = get(node.key) as N
        if (n is SelectorImpl<*>) {
//            if (!n.isValid) {
                // TODO: return completable deferred for the value of the Node.
//                return
//            }
        }
        return n.value
    }

    /**
     * A key for identifying an instance of a [Node].
     * The object made with this interface will allow for polymorphic retrieval and is light weight.
     */
    public interface Key <N : Node>

    // TODO: implement operator fun plus to combine two contexts;
    //  removing duplicate keys from this, in favor of [other] (more recent)

//    operator fun plus(other: RekoilContext): RekoilContext

    // TODO: probably also want minus operator to obtain new context that excludes nodes.

    /**
     * A node of the [RekoilContext]. A node of the rekoil context is a singleton context by itself.
     * Providing the single source of truth within any rekoil context.
     */
    public interface Node : RekoilContext {
        /**
         * A key of this rekoil context node.
         */
        public val key: Key<*>

        // Base case: return our node (with typecast) if we match the key.
        public override operator fun <N : Node> get(key: Key<N>): N? =
            @Suppress("UNCHECKED_CAST")
            if (this.key == key) this as N else null

        /**
         * Invalidate the data within the current node.
         */
        public fun invalidate()
    }

    public interface ValueNode<T> : Node {
        /**
         * The getter returns the cached value of the Node.
         * The setter will cache and publish the new value to all observers.
         */
        public var value: T

        /**
         * Asynchronously subscribes to this [ValueNode].
         * This will immediately emit the current value, and will continue to emit values until
         * the CoroutineScope of the Node is destroyed or [release] is called.
         *
         * A [Job] is returned so that the subscriber can be cancelled if desired.
         * TODO: use custom interface that only exposes cancel?
         */
        @ExperimentalCoroutinesApi
        public fun subscribe(onValueChanged: (T) -> Unit): Job

        /**
         * Shut down this node. Cleaning up any channels created for emitting values to subscribers.
         */
        public fun release()

        /**
         * Called when the [ValueNode] has been released.
         * Passing through the last value for the Node.
         */
        public fun onRelease(block: (T) -> Unit)

        /*
         * Identifier for the toString() method. This forces the default hash code preventing any
         * implementations from overriding the hashCode() method and breaking functionality.
         */
        val identifier: String get() = Integer.toHexString(System.identityHashCode(key))
    }
}

//object KeyGenerator {
//    fun generate(name: String? = null): RekoilContext.Key<Any> {
//        return name?.run {
//            object : RekoilContext.Key {
//                override fun toString(): String {
//                    return "RekoilContext.Key(generatedWithName=$this)"
//                }
//            }
//        } ?: object : RekoilContext.Key<Any>
//    }
//}