[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [SelectorScope](./index.md)

# SelectorScope

`interface SelectorScope : `[`RekoilScope`](../-rekoil-scope/index.md)

### Functions

| Name | Summary |
|---|---|
| [get](get.md) | Get the value of the passed node, registering the dependency with the parent scope.`abstract suspend fun <R> get(node: ValueNode<R>): R` |
| [release](release.md) | `abstract fun release(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Extension Functions

| Name | Summary |
|---|---|
| [async](../async.md) | `fun `[`RekoilScope`](../-rekoil-scope/index.md)`.async(block: suspend `[`RekoilScope`](../-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
| [launch](../launch.md) | Launches a new RekoilScope in the background on the provided CoroutineScope`fun `[`RekoilScope`](../-rekoil-scope/index.md)`.launch(block: suspend `[`RekoilScope`](../-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
