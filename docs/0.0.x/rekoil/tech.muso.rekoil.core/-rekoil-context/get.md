[rekoil](../../index.md) / [tech.muso.rekoil.core](../index.md) / [RekoilContext](index.md) / [get](./get.md)

# get

`abstract operator fun <N : Node> get(key: Key<N>): N?`

Returns the node with the given [key](get.md#tech.muso.rekoil.core.RekoilContext$get(tech.muso.rekoil.core.RekoilContext.Key((tech.muso.rekoil.core.RekoilContext.get.N)))/key) from this context or `null` if not present.

`open fun <R, N : ValueNode<R>> get(node: N): R`

Returns the cached node value if it is within this context.

