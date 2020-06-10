# Scopes

Scopes are an expansion of the original Recoil design. They allow for [Atoms](atoms.md) 
and [Selectors](selectors.md) to be defined within their own private context.

[RekoilScopes]() are designed to operate similarly to the Kotlin standard library
[Scope Functions](https://kotlinlang.org/docs/reference/scope-functions.html). \
Atoms and Selectors created within a scope are currently not shareable across scopes.

### Basics

The RekoilScope itself is always created with a CoroutineScope for the execution of internal asynchronous communication
of updates between Atoms and Selectors.

```kotlin
val rekoilScope = RekoilScope(coroutineScope)
```

The scope that it is created on manages all the coroutines so that when the 
Coroutine Scope gets cancelled, the RekoilScope and the components within it
automatically stop, without leaking.

__WARNING:__ if you create a RekoilScope on a CoroutineScope that leaks, the RekoilScope itself will leak.\
To prevent leaking on a CoroutineScope that will not be cancelled, `RekoilScope.release()` can be called.  

### Child Scopes

Selectors automatically generate their own Scope for convenience.
This allows for you to define your own internal Atoms and Selectors that do not expose themselves to the parent scope.
Be careful with your current implementation of these, as there is no internal checking mechanism to determine "liveness"
of an Atom/Selector. This is planned, but as of right now to free children within a Selector, call `Job.cancel()` on the
Selector since it generates a new CoroutineScope.

```kotlin
rekoilScope {
    atom { "parent scope" }
    selector { // generates its own child scope
        val internalAtom = atom { "from internal scope" }
        val internalSelector = selector { get(internalAtom).length }
        internalAtom.value = "update from internal"
    }
}
```

#### Specify Coroutine Scope

Any Node (Atom / Selector) can specify it's own Coroutine scope by overwriting the default, which is inherited by the
parent.

```kotlin
rekoilScope {
    var ioCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    atom(coroutineScope = ioCoroutineScope) { "default value" } // will affect scope/thread used for subscribe updates. 
    selector(coroutineScope = ioCoroutineScope) { some io operation }

    ioCoroutineScope.cancel() // cancel scope and stop monitoring nodes with this coroutine scope.
}
```