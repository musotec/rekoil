# Atoms

Atoms are a type of Node that contains a single source of truth for a value __within the current scope__.

To create an Atom you can use the DSL within any [RekoilScope](scopes.md).

```kotlin
suspend fun main() = rekoilScope(launch = true) {
    val a1 = atom { "default value" }
    ...
}
```

The simple expression automatically infers the type automatically based on the expression.
The above case gets type `Atom<String>`.

Additionally, it is worth noting that a unique Key for the Atom is automatically generated for you.
There is no need to manage the key on your own, as it is handled by the library. _[Other differences from Recoil.js]()_

_In the future Keys may be passed to obtain Atoms & Selectors across a global application scope._

## Updating Values

Atoms can be updated within the scope using the property accessor.
```kotlin
a1.value = "new value"      // this is equivalent to a1.setValue("new value")
```

Under the hood, this will automatically handle updating the cached value within the scope(s) it belongs to, along 
with sending the updated value to all currently active [subscribers](). The scope(s)  dependents on the Atom.

### Functionally

If you wish to obtain the setter function similar to the JS version, you can do so as such.

```kotlin
rekoilScope {
    ...
    // obtain setter function
    val setValue = a1::value.setter
    // invoke function via reflection
    setValue("new value")
}
```

It is important to note that this will obtain the setter via reflection.
Additional reference for usage can be found in the documentation on Kotlin [function types](https://kotlinlang.org/docs/reference/lambdas.html#invoking-a-function-type-instance).

## Retrieving Values (scopeless)

The __cached value__ can be obtained from any Atom, without scope, using the same `value` property.

```kotlin
val atomValue = a1.value
``` 

To receive the new Atom values __asynchronously__, attach a [subscriber]().

#### Incorrect Usage

The following does not work in the RekoilScope by design.
```kotlin
val incorrectUsage = get(a1)
```
This is so that the developer is aware of what they are doing behind the scenes.
The `get(Atom)` call can only be done from within a [Selector](selectors.md).

## Receiving Updates

New Values sent to an Atom (or selector) can be subscribed to. Even outside of the scope.
```kotlin
rekoilScope {
    val atom1 = atom { 0 }

    // subscribe first
    atom1.subscribe {
        println("received: i")
    }

    // send new values
    for (i in 1..5) {
        atom1.value = i
    }
}
```