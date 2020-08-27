[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [SupervisorScopeImpl](index.md) / [withScope](./with-scope.md)

# withScope

`open fun <R> withScope(borrowedRekoilScope: `[`RekoilScope`](../-rekoil-scope/index.md)`, coroutineScope: CoroutineScope, key: Key<`[`Selector`](../-selector/index.md)`<R>>?, value: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> R): `[`Selector`](../-selector/index.md)`<R?>`

Generate a Selector within the current RekoilScope, inheriting the RekoilScope passed.

