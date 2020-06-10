[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [Selector](index.md) / [changeSelector](./change-selector.md)

# changeSelector

`abstract fun <T : R> changeSelector(selectorScope: suspend `[`SelectorScope`](../-selector-scope/index.md)`.() -> T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Set the new value for the Selector.
This will recompute the dependencies for this node.

