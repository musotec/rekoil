[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [Atom](index.md) / [setValueAsync](./set-value-async.md)

# setValueAsync

`abstract fun setValueAsync(value: suspend () -> T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Set the new value for the Atom asynchronously,
invalidating observers instantly, and notifying the RekoilScope upon completion.

