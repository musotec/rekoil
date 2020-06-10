package tech.muso.rekoil.test

import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import tech.muso.rekoil.core.Atom
import tech.muso.rekoil.core.RekoilScope
import tech.muso.rekoil.core.launch
import tech.muso.rekoil.core.rekoilScope
import tech.muso.rekoil.test.helpers.TestRekoilScope
import tech.muso.rekoil.test.helpers.rekoilScopeTest


class RekoilScopeTest {

    @Test
    fun testAsyncRekoilScope() {
        // this only tests the creation, not the successful execution of internal assertions
        TestCoroutineScope().apply {
            async {
                exampleRekoilScope()
            }

            advanceTimeBy(10_000)
        }
    }

    @Test
    fun testConstructorRekoilScope() {
        exampleRekoilScopeConstructor()
    }

    // TODO: fix multiple threaded dispatchers receiving multiple updates from subscribe.
    fun exampleRekoilScopeConstructor() {
        var outsideAtom: Atom<Int>? = null

        RekoilScope(coroutineScope = CoroutineScope(Dispatchers.IO)).launch {
            val a1 = atom { 1 }
            val a2 = atom { 2f }
            val a3 = atom { false }

            outsideAtom = atom { 200 }
            outsideAtom?.subscribe {
                println("outsideAtom: $it")
            }

            println(outsideAtom?.value)
        }

        rekoilScopeTest {
//            println(outsideAtom?.value)

            val atom1 = atom { 22 }
            val selector = selector { get(atom1) } // + get(outsideAtom!!) }
            selector.subscribe {
                println("selector: $it")
            }

            atom1.value = 21
            outsideAtom?.value = 100


//            outsideAtom::value!!.set
        }
    }

    suspend fun exampleRekoilScope() =
        rekoilScope(launch = true) {
            val a1= atom { "A" }
            val a2 = atom { "B" }
            val s1 = selector { get(a1) + get(a2) }
            s1.subscribe {
                println("s1 emits: `$it`")
            }

            // the following does not work in the RekoilScope by design.
//            a1.value = get(a2)

            // this is so that a developer is more aware of what they are doing behind the scenes

            // the current cached value can be retrieved from any where,
            // val cachedValue = a2.value

            // set value by querying a2's cached value
            a1.value = a2.value

//            get(a1)

            // selector s1 immediately receives the update

            selector {
                val newScopedAtom = atom { "newScopedAtom" }

                // get(node) returns the value from the scope that defines the selector.
//                val fetchedA1 = get(a1)
//                val fetchedA2 = get(a2)

                // if we define a new Atom or Selector, they will exist only within this scope
                val newScopedSelector
                        = selector {
                            // cannot do the following, since they are outside the scope of this selector.
                          val fetchedA1 = get(a1)
                          val fetchedA2 = get(a2)
                            get(newScopedAtom) + " " + fetchedA1 + fetchedA2
                        }.also {
                            it.subscribe { println("newScopedSelector: $it") }
                        }

                get(newScopedAtom) + " combined with " + get(newScopedSelector)
            }.apply {
                println("anonymous selector value upon creation: ${this.value}")
                subscribe {
                    println("anonymous selector emits: `$it`")
                }
            }



            delay(10000L)
        }

    @Test
    fun testCancellation() = rekoilScopeTest {
        val testAtom = atom {
            listOf("this", "is", "a", "test")
        }

        testAtom.subscribe {
            println(it.joinToString(" "))
        }
    }


    @Test
    fun testDelayedResult() = rekoilScopeTest {
        val testAtom = atom {
            "default"
        }
    }

    @Test
    fun genericTest() = runBlockingTest {
        RekoilScope(this).launch {
            val testAtom1 = atom {
                listOf("this", "is", "a", "test", "list", "of", "strings")
            }

            val testAtom2 = atom {
                "String"
            }

            val testEmptyAtom = atom { }

            val testSuspendingAtom = atom {
//                delay(100)
                "delayed 100ms"
            }

            // provided function must return a value.
//            val invalidAtom = atom { Nothing }


            val selector1 = selector {
                val fetchAtom1: List<String> = get(testAtom1)
                val fetchAtom2 = get(testAtom2)
                fetchAtom1.map { it.length * fetchAtom2.length }
            }

            val j1 = async {
                testAtom1.subscribe {
                    println(it.joinToString(" "))
                }
            }

            val j2 = async {
                selector1.subscribe {
                    println(it)
                }
            }

            testAtom1.value = listOf("mutated", "list")
            delay(1000)

            testAtom1.value = listOf("mutated", "list1")
            delay(1000)

            testAtom1.value = listOf("mutated", "list2")
            delay(1000)

            testAtom1.value = listOf("mutated", "list3")
            delay(1000)

            val j3 = async {
                selector1.subscribe {
                    println("selector1: $it")
                }
            }

            println("TEST")

            // release for our test scope to pass
            this.release()
        }
    }

}