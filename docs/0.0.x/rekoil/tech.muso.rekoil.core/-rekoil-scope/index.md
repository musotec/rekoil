[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [RekoilScope](./index.md)

# RekoilScope

`interface RekoilScope`

Defines a scope for a new Rekoil dependency graph.

A RekoilScope provides access to the creation methods [atom](atom.md) and [selector](selector.md)
while also encapsulating the [CoroutineScope](#) that should control execution.

Every RekoilScope contains a [RekoilContext](../-rekoil-context/index.md) which contains the [RekoilContext.get](../-rekoil-context/get.md)
method.

### Properties

| Name | Summary |
|---|---|
| [rekoilContext](rekoil-context.md) | `abstract val rekoilContext: `[`RekoilContext`](../-rekoil-context/index.md) |

### Functions

| Name | Summary |
|---|---|
| [atom](atom.md) | Generate an Atom within the RekoilScope`abstract fun <R : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> atom(coroutineScope: CoroutineScope = rekoilContext.coroutineScope, key: Key<`[`Atom`](../-atom/index.md)`<R>>? = null, isAsync: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true, cache: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true, value: () -> R): `[`Atom`](../-atom/index.md)`<R>` |
| [releaseScope](release-scope.md) | Release all the nodes within this RekoilScope.`abstract fun releaseScope(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [selector](selector.md) | Generate a Selector within the RekoilScope. The selector returns a new RekoilScope for scoping within the Selector only.`abstract fun <R> selector(coroutineScope: CoroutineScope = rekoilContext.coroutineScope, key: Key<`[`Selector`](../-selector/index.md)`<R>>? = null, value: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> R): `[`Selector`](../-selector/index.md)`<R?>` |
| [withScope](with-scope.md) | Generate a Selector within the current RekoilScope, inheriting the RekoilScope passed.`abstract fun <R> withScope(borrowedRekoilScope: `[`RekoilScope`](./index.md)`, coroutineScope: CoroutineScope = rekoilContext.coroutineScope, key: Key<`[`Selector`](../-selector/index.md)`<R>>? = null, value: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> R): `[`Selector`](../-selector/index.md)`<R?>` |

### Extension Functions

| Name | Summary |
|---|---|
| [async](../async.md) | `fun `[`RekoilScope`](./index.md)`.async(block: suspend `[`RekoilScope`](./index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
| [launch](../launch.md) | Launches a new RekoilScope in the background on the provided CoroutineScope`fun `[`RekoilScope`](./index.md)`.launch(block: suspend `[`RekoilScope`](./index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |

### Inheritors

| Name | Summary |
|---|---|
| [Selector](../-selector/index.md) | `interface Selector<R> : ValueNode<R>, `[`RekoilScope`](./index.md)`, `[`CoroutineContext`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) |
| [SelectorScope](../-selector-scope/index.md) | `interface SelectorScope : `[`RekoilScope`](./index.md) |
| [SupervisorScopeImpl](../-supervisor-scope-impl/index.md) | `open class SupervisorScopeImpl : `[`RekoilScope`](./index.md) |
