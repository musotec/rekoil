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
| [getValue](../../tech.muso.rekoil.ktx/get-value.md) | `operator fun <T> ValueNode<T>.getValue(line: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, property: `[`KProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)`<*>): T` |
| [setValue](../../tech.muso.rekoil.ktx/set-value.md) | Create extension function for setValue for automatic kotlin delegate properties.`operator fun <T> ValueNode<T>.setValue(line: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, property: `[`KProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)`<*>, t: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
