[rekoil](../../../index.md) / [tech.muso.rekoil.core](../../index.md) / [RekoilContext](../index.md) / [ValueNode](index.md) / [subscribe](./subscribe.md)

# subscribe

`@ExperimentalCoroutinesApi abstract fun subscribe(onValueChanged: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job`

Asynchronously subscribes to this [ValueNode](index.md).
This will immediately emit the current value, and will continue to emit values until
the CoroutineScope of the Node is destroyed or [release](release.md) is called.

A [Job](#) is returned so that the subscriber can be cancelled if desired.
TODO: use custom interface that only exposes cancel?

