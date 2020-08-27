[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [SupervisorScopeImpl](./index.md)

# SupervisorScopeImpl

`open class SupervisorScopeImpl : `[`RekoilScope`](../-rekoil-scope/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SupervisorScopeImpl(parent: `[`RekoilContext`](../-rekoil-context/index.md)`?, rootNode: Node, coroutineScope: CoroutineScope)` |

### Properties

| Name | Summary |
|---|---|
| [coroutineScope](coroutine-scope.md) | `val coroutineScope: CoroutineScope` |
| [parent](parent.md) | `val parent: `[`RekoilContext`](../-rekoil-context/index.md)`?` |
| [rekoilContext](rekoil-context.md) | `open val rekoilContext: `[`RekoilContext`](../-rekoil-context/index.md) |

### Functions

| Name | Summary |
|---|---|
| [atom](atom.md) | Generate an Atom within the RekoilScope`open fun <R : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> atom(coroutineScope: CoroutineScope, key: Key<`[`Atom`](../-atom/index.md)`<R>>?, isAsync: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, cache: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, value: () -> R): `[`Atom`](../-atom/index.md)`<R>` |
| [releaseScope](release-scope.md) | Release all the nodes within this RekoilScope.`open fun releaseScope(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [selector](selector.md) | Generate a Selector within the RekoilScope. The selector returns a new RekoilScope for scoping within the Selector only.`open fun <R> selector(scope: CoroutineScope, key: Key<`[`Selector`](../-selector/index.md)`<R>>?, value: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> R): `[`Selector`](../-selector/index.md)`<R?>` |
| [withScope](with-scope.md) | Generate a Selector within the current RekoilScope, inheriting the RekoilScope passed.`open fun <R> withScope(borrowedRekoilScope: `[`RekoilScope`](../-rekoil-scope/index.md)`, coroutineScope: CoroutineScope, key: Key<`[`Selector`](../-selector/index.md)`<R>>?, value: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> R): `[`Selector`](../-selector/index.md)`<R?>` |

### Extension Functions

| Name | Summary |
|---|---|
| [async](../async.md) | `fun `[`RekoilScope`](../-rekoil-scope/index.md)`.async(block: suspend `[`RekoilScope`](../-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
| [launch](../launch.md) | Launches a new RekoilScope in the background on the provided CoroutineScope`fun `[`RekoilScope`](../-rekoil-scope/index.md)`.launch(block: suspend `[`RekoilScope`](../-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): Job` |
