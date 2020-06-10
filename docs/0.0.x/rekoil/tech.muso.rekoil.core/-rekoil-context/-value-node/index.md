[rekoil](../../../index.md) / [tech.muso.rekoil.core](../../index.md) / [RekoilContext](../index.md) / [ValueNode](./index.md)

# ValueNode

`interface ValueNode<T> : Node`

### Properties

| Name | Summary |
|---|---|
| [identifier](identifier.md) | `open val identifier: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.md) | The getter returns the cached value of the Node. The setter will cache and publish the new value to all observers.`abstract var value: T` |

### Functions

| Name | Summary |
|---|---|
| [onRelease](on-release.md) | Called when the [ValueNode](./index.md) has been released. Passing through the last value for the Node.`abstract fun onRelease(block: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [release](release.md) | Shut down this node. Cleaning up any channels created for emitting values to subscribers.`abstract fun release(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [subscribe](subscribe.md) | Asynchronously subscribes to this [ValueNode](./index.md). This will immediately emit the current value, and will continue to emit values until the CoroutineScope of the Node is destroyed or [release](release.md) is called.`abstract fun subscribe(onValueChanged: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
