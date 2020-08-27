[rekoil](../../index.md) / [tech.muso.rekoil.ktx](../index.md) / [FibonacciHeap2](./index.md)

# FibonacciHeap2

`class FibonacciHeap2<T : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<T>>`

### Types

| Name | Summary |
|---|---|
| [HeapProperty](-heap-property/index.md) | `enum class HeapProperty` |
| [Node](-node/index.md) | `data class Node<T : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<T>> : `[`Iterable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)`<Node<T>>` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `FibonacciHeap2(type: HeapProperty = HeapProperty.MINIMUM)` |

### Properties

| Name | Summary |
|---|---|
| [compare](compare.md) | `val compare: (`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [totalNodes](total-nodes.md) | `var totalNodes: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [add_node_to_root_list](add_node_to_root_list.md) | `fun add_node_to_root_list(node: Node<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [cascading_cut](cascading_cut.md) | To improve time bounds, perform on a parent`tailrec fun cascading_cut(node: Node<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [compare](compare.md) | `fun compare(node: Node<T>, rootNode: Node<T>): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [consolidate](consolidate.md) | Combine root nodes of equal degree to consolidate the heap by creating a list of unordered binomial trees`fun consolidate(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [cut](cut.md) | If a child node becomes smaller than its parent node, abandon child, and move it to the root list.`fun cut(child: Node<T>, parent: Node<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [decrease_key](decrease_key.md) | `fun decrease_key(node: Node<T>, data: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [heap_link](heap_link.md) | Link one node to another in the root list, and update its child list to reflect the move.`fun heap_link(y: Node<T>, x: Node<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [insert](insert.md) | Insert into the unordered root list with O(1) time. Immediately caching a new minimum.`fun insert(data: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [merge_with_child_list](merge_with_child_list.md) | `fun merge_with_child_list(parent: Node<T>, node: Node<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [remove_from_root_list](remove_from_root_list.md) | `fun remove_from_root_list(node: Node<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
