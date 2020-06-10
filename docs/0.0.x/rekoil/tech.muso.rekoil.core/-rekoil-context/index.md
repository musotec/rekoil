[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [RekoilContext](./index.md)

# RekoilContext

`interface RekoilContext`

### Types

| Name | Summary |
|---|---|
| [Key](-key.md) | A key for identifying an instance of a [Node](-node/index.md). The object made with this interface will allow for polymorphic retrieval and is light weight.`interface Key<N : Node>` |
| [Node](-node/index.md) | A node of the [RekoilContext](./index.md). A node of the rekoil context is a singleton context by itself. Providing the single source of truth within any rekoil context.`interface Node : `[`RekoilContext`](./index.md) |
| [ValueNode](-value-node/index.md) | `interface ValueNode<T> : Node` |

### Properties

| Name | Summary |
|---|---|
| [coroutineScope](coroutine-scope.md) | The CoroutineScope to execute async calls for nodes within this context.`abstract val coroutineScope: CoroutineScope` |

### Functions

| Name | Summary |
|---|---|
| [get](get.md) | Returns the node with the given [key](get.md#tech.muso.rekoil.core.RekoilContext$get(tech.muso.rekoil.core.RekoilContext.Key((tech.muso.rekoil.core.RekoilContext.get.N)))/key) from this context or `null` if not present.`abstract operator fun <N : Node> get(key: Key<N>): N?`<br>Returns the cached node value if it is within this context.`open fun <R, N : ValueNode<R>> get(node: N): R` |
