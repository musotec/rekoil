[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [RekoilScope](index.md) / [atom](./atom.md)

# atom

`abstract fun <R : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> atom(coroutineScope: CoroutineScope = rekoilContext.coroutineScope, key: Key<`[`Atom`](../-atom/index.md)`<R>>? = null, isAsync: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true, cache: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true, value: () -> R): `[`Atom`](../-atom/index.md)`<R>`

Generate an Atom within the RekoilScope

### Parameters

`key` - if null, the default key will be used.