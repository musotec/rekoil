[rekoil](../../../index.md) / [tech.muso.rekoil.core](../../index.md) / [RekoilContext](../index.md) / [Node](./index.md)

# Node

`interface Node : `[`RekoilContext`](../index.md)

A node of the [RekoilContext](../index.md). A node of the rekoil context is a singleton context by itself.
Providing the single source of truth within any rekoil context.

### Properties

| Name | Summary |
|---|---|
| [key](key.md) | A key of this rekoil context node.`abstract val key: Key<*>` |

### Functions

| Name | Summary |
|---|---|
| [get](get.md) | Returns the node with the given [key](../get.md#tech.muso.rekoil.core.RekoilContext$get(tech.muso.rekoil.core.RekoilContext.Key((tech.muso.rekoil.core.RekoilContext.get.N)))/key) from this context or `null` if not present.`open operator fun <N : Node> get(key: Key<N>): N?` |
| [invalidate](invalidate.md) | Invalidate the data within the current node.`abstract fun invalidate(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
