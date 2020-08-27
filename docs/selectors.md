# Selectors

> A __selector__ represents a piece of __derived state__.
You can think of derived state as the output of passing state to a pure function that modifies the given state in some way. [[1]](https://recoiljs.org/docs/basic-tutorial/selectors)

### Writeable Selectors
_Coming Soon_ 

### Asynchronicity
Instead of returning Promise for an asynchronous evaluation, the SelectorScope is 
automatically launched in a background coroutine. Any suspending function calls 
will be run asynchronously.

#### Registration with _get_(Node)
The `get(Node)` function is a __suspending function__ that creates a __dependency__.\
The dependency tells the parent [Scope](scopes.md) that when the Node updates, the 
Selector should recompute.

```kotlin
rekoilScope {
    val atom1 = atom { 0 }
    val selector1 = selector { 
        val x = get(atom1)      // registers dependency to receive updates
        return@selector x * x   // return x^2
    }

    selector1.subscribe {
        println("received: $it")
    }

    for (i in 1..5) {
        println("sending: $i")
        atom1.value = i
    }
}
```

If the called Node (Atom or Selector)
is still updating, the caller (and other dependents) will receive the updated value immediately
upon it's arrival to the RekoilScope behind the scenes.

__Cached value reading:__\
Any Node can be read without automatically registering  