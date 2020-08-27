[rekoil](../../../index.md) / [tech.muso.rekoil.ktx](../../index.md) / [FibonacciHeap2](../index.md) / [Node](./index.md)

# Node

`data class Node<T : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<T>> : `[`Iterable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)`<Node<T>>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Node(value: T, degree: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0, parent: Node<T>? = null, child: Node<T>? = null, mark: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)` |

### Properties

| Name | Summary |
|---|---|
| [child](child.md) | `var child: Node<T>?` |
| [degree](degree.md) | `var degree: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [left](left.md) | `var left: Node<T>` |
| [mark](mark.md) | `var mark: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [parent](parent.md) | `var parent: Node<T>?` |
| [right](right.md) | `var right: Node<T>` |
| [value](value.md) | `var value: T` |

### Functions

| Name | Summary |
|---|---|
| [forEach](for-each.md) | `fun forEach(action: Consumer<in Node<T>>?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [iterate](iterate.md) | Iterate over the doubly linked list of a given node. Yielding the head node last.`fun iterate(): `[`Sequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)`<Node<T>?>` |
| [iterator](iterator.md) | `fun iterator(): `[`Iterator`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterator/index.html)`<Node<T>>` |
