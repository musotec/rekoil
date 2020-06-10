[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [Selector](./index.md)

# Selector

`@ExperimentalCoroutinesApi interface Selector<R> : ValueNode<R>, `[`RekoilScope`](../-rekoil-scope/index.md)`, `[`CoroutineContext`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html)

### Functions

| Name | Summary |
|---|---|
| [changeSelector](change-selector.md) | Set the new value for the Selector. This will recompute the dependencies for this node.`abstract fun <T : R> changeSelector(selectorScope: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Extension Functions

| Name | Summary |
|---|---|
| [async](../async.md) | `fun `[`RekoilScope`](../-rekoil-scope/index.md)`.async(block: suspend `[`RekoilScope`](../-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
| [getPolymorphicElement](../get-polymorphic-element.md) | Used to re-obtain type from the abstract key so that type inference works at compile time.`fun <N : Node> Node.getPolymorphicElement(key: Key<N>): N?` |
| [launch](../launch.md) | Launches a new RekoilScope in the background on the provided CoroutineScope`fun `[`RekoilScope`](../-rekoil-scope/index.md)`.launch(block: suspend `[`RekoilScope`](../-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
