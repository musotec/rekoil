[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [Atom](./index.md)

# Atom

`interface Atom<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> : ValueNode<T>`

### Functions

| Name | Summary |
|---|---|
| [setValueAsync](set-value-async.md) | Set the new value for the Atom asynchronously, invalidating observers instantly, and notifying the RekoilScope upon completion.`abstract fun setValueAsync(value: suspend () -> T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Extension Functions

| Name | Summary |
|---|---|
| [getPolymorphicElement](../get-polymorphic-element.md) | Used to re-obtain type from the abstract key so that type inference works at compile time.`fun <N : Node> Node.getPolymorphicElement(key: Key<N>): N?` |
