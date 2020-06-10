[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [RekoilScope](index.md) / [selector](./selector.md)

# selector

`abstract fun <R> selector(coroutineScope: CoroutineScope = rekoilContext.coroutineScope, key: Key<`[`Selector`](../-selector/index.md)`<R>>? = null, value: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> R): `[`Selector`](../-selector/index.md)`<R?>`

Generate a Selector within the RekoilScope.
The selector returns a new RekoilScope for scoping within the Selector only.

