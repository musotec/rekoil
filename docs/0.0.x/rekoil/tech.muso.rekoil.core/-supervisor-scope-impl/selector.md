[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [SupervisorScopeImpl](index.md) / [selector](./selector.md)

# selector

`open fun <R> selector(scope: CoroutineScope, key: Key<`[`Selector`](../-selector/index.md)`<R>>?, value: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> R): `[`Selector`](../-selector/index.md)`<R?>`

Generate a Selector within the RekoilScope.
The selector returns a new RekoilScope for scoping within the Selector only.

