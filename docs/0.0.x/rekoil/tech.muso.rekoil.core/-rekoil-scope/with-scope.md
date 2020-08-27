[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [RekoilScope](index.md) / [withScope](./with-scope.md)

# withScope

`abstract fun <R> withScope(borrowedRekoilScope: `[`RekoilScope`](index.md)`, coroutineScope: CoroutineScope = rekoilContext.coroutineScope, key: Key<`[`Selector`](../-selector/index.md)`<R>>? = null, value: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> R): `[`Selector`](../-selector/index.md)`<R?>`

Generate a Selector within the current RekoilScope, inheriting the RekoilScope passed.

