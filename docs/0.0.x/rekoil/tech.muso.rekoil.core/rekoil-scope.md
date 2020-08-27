[rekoil](../index.md) / [tech.muso.rekoil.core](index.md) / [rekoilScope](./rekoil-scope.md)

# rekoilScope

`suspend fun rekoilScope(launch: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, block: suspend `[`RekoilScope`](-rekoil-scope/index.md)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Creates a new [RekoilScope](-rekoil-scope/index.md) and calls the specified suspend block with this scope.
The provided scope inherits the [coroutineContext](#)
from the outer scope, following the execution pattern of the [coroutineScope](#) Contract Builder.

If [launch](rekoil-scope.md#tech.muso.rekoil.core$rekoilScope(kotlin.Boolean, kotlin.coroutines.SuspendFunction1((tech.muso.rekoil.core.RekoilScope, kotlin.Unit)))/launch) is true, then the RekoilScope will be launched into a new scope.
Then the RekoilScope will require a manual [RekoilScope.releaseScope](-rekoil-scope/release-scope.md) call.

