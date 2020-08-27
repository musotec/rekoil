[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [SupervisorScopeImpl](index.md) / [atom](./atom.md)

# atom

`open fun <R : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> atom(coroutineScope: CoroutineScope, key: Key<`[`Atom`](../-atom/index.md)`<R>>?, isAsync: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, cache: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, value: () -> R): `[`Atom`](../-atom/index.md)`<R>`

Generate an Atom within the RekoilScope

### Parameters

`key` - if null, the default key will be used.