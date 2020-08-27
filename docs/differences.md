## Differences from [recoil.js](https://recoiljs.org/)

The Rekoil implementation is different from the [facebookexperimental/Recoil](https://github.com/facebookexperimental/Recoil) (as of 6/12/20).

This is for one of two reasons:
* the upstream feature is undocumented
* Kotlin has syntax improvements without affecting performance

Here, I have chosen to keep the implementation close to the Kotlin standard library 
so that implementing this library is easy. For this reason, the syntax is very similar
to the DSLs used in Kotlin Coroutines. This way the asynchronous code written in Kotlin
using comingled Coroutines and Suspend functions looks and writes nearly identically.  

### Scopes
Following the syntax of Coroutines, Rekoil defines Scopes to scope the code within them.
Atoms and Selectors can only be made within a RekoilScope using the Rekoil DSL.

__[RekoilScope](scopes.md)__
```kotlin
suspend fun main() = rekoilScope {
    launch {
        atom { ... }
        selector { ... }
    }
}
```

__[CoroutineScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html)__
```kotlin
suspend fun showSomeData() = coroutineScope {
    launch { // <- launch a new coroutine on current scope
        ... perform background computation ...
    }
}
```

Although Recoil.js does not have the ability to define custom scopes, internally a global 
state structure [`Store`](https://github.com/facebookexperimental/Recoil/blob/master/src/core/Recoil_State.js)
exists which manages the state (as of 6/12/20).

// TODO: CIRCULAR DEPENDENCY RESOLUTION

#### Asynchronicity
Since RekoilScopes are created with a CoroutineScope, they are asynchronous by default.\
To control what thread they run on, provide them a specific scope. See [Scopes](scopes.md).


#### Value Observation
In Rekoil, nodes (Atoms and Selectors) to be observed using a
[BroadcastChannel](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-broadcast-channel/).

Despite their omission from the official Kotlin [language guide on Channels](https://kotlinlang.org/docs/reference/coroutines/channels.html), 
BroadcastChannels are
* low weight and asynchronous
* provide a one (sender) to many (receivers) relationship
* a member of _kotlinx-coroutines-core_
  * will be implemented in Kotlin/JS and Kotlin/Native
* Superior to Actors which
  * currently require the JVM version
  * are likely to be changed according to comments in source code

Since this construct is in _kotlinx-coroutines-core_, it is (or will be) supported in
Kotlin/JS and Kotlin/Native.
