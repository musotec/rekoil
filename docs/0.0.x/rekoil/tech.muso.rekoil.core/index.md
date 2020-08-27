[rekoil](../index.md) / [tech.muso.rekoil.core](./index.md)

## Package tech.muso.rekoil.core

### Types

| Name | Summary |
|---|---|
| [AbstractRekoilContextKey](-abstract-rekoil-context-key/index.md) | Used internally. TODO: create documentation when supporting library extension.`abstract class AbstractRekoilContextKey<B : Node, N : B> : Key<N>` |
| [Atom](-atom/index.md) | `interface Atom<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> : ValueNode<T>` |
| [RekoilContext](-rekoil-context/index.md) | `interface RekoilContext` |
| [RekoilScope](-rekoil-scope/index.md) | Defines a scope for a new Rekoil dependency graph.`interface RekoilScope` |
| [Selector](-selector/index.md) | `interface Selector<R> : ValueNode<R>, `[`RekoilScope`](-rekoil-scope/index.md)`, `[`CoroutineContext`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) |
| [SelectorScope](-selector-scope/index.md) | `interface SelectorScope : `[`RekoilScope`](-rekoil-scope/index.md) |
| [SupervisorScopeImpl](-supervisor-scope-impl/index.md) | `open class SupervisorScopeImpl : `[`RekoilScope`](-rekoil-scope/index.md) |

### Functions

| Name | Summary |
|---|---|
| [async](async.md) | `fun `[`RekoilScope`](-rekoil-scope/index.md)`.async(block: suspend `[`RekoilScope`](-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
| [getPolymorphicElement](get-polymorphic-element.md) | Used to re-obtain type from the abstract key so that type inference works at compile time.`fun <N : Node> Node.getPolymorphicElement(key: Key<N>): N?` |
| [launch](launch.md) | Launches a new RekoilScope in the background on the provided CoroutineScope`fun `[`RekoilScope`](-rekoil-scope/index.md)`.launch(block: suspend `[`RekoilScope`](-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
| [rekoilScope](rekoil-scope.md) | Creates a new [RekoilScope](-rekoil-scope/index.md) and calls the specified suspend block with this scope. The provided scope inherits the [coroutineContext](#) from the outer scope, following the execution pattern of the [coroutineScope](#) Contract Builder.`suspend fun rekoilScope(launch: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, block: suspend `[`RekoilScope`](-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [RekoilScope](-rekoil-scope.md) | `fun RekoilScope(coroutineScope: CoroutineScope): `[`RekoilScope`](-rekoil-scope/index.md)<br>`fun RekoilScope(coroutineDispatcher: CoroutineDispatcher): `[`RekoilScope`](-rekoil-scope/index.md) |
