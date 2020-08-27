package tech.muso.rekoil.test

import junit.framework.TestCase.fail
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import tech.muso.rekoil.RekoilDependencyException
import tech.muso.rekoil.core.Atom
import tech.muso.rekoil.core.RekoilScope
import tech.muso.rekoil.core.launch
import tech.muso.rekoil.core.rekoilScope
import tech.muso.rekoil.test.helpers.assertEquals
import tech.muso.rekoil.test.helpers.rekoilScopeTest

class RekoilScopeTests {

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
                // map number of letters in string * [1] (==>len(atom2))
                fetchAtom1.map { it.length * fetchAtom2.length }
            }

            val j1 =
                testAtom1.subscribe {
                    println(it.joinToString(" "))
                }


            val j2 =
                selector1.subscribe {
                    println(it)
                }


            testAtom1.value = listOf("mutated", "list")
            delay(1000)

            testAtom1.value = listOf("mutated", "list1")
            delay(1000)

            testAtom1.value = listOf("mutated", "list2")
            delay(1000)

            testAtom1.value = listOf("mutated", "list3")
            delay(1000)

            val j3 =
                selector1.subscribe {
                    println("selector1: $it")
                }

            println("TEST")

            // release for our test scope to pass
            this.releaseScope()
        }
    }

    @Test
    fun testScopeInheritance() = rekoilScopeTest {
        val readTestString = "read me"
        val atom1 = atom { readTestString }
        selector {
            val readString = get(atom1)
            println(readString)
            assertEquals(readTestString, readString)

            selector {
                val readStringFromInheritedScope = get(atom1)
                println(readStringFromInheritedScope)
                assertEquals(readTestString, readStringFromInheritedScope)

                // release for our test scope to pass,
                // otherwise would listen forever (test scope finishes with active jobs)
//                releaseScope()
            }
        }
    }

    @Test
    fun testManyFastUpdates() = rekoilScopeTest {

        val sharedAtom = atom { true }
        var receivedUpdates = 0
        val count = 500

        selector {
            val value = get(sharedAtom)
            println("get(${value}) : $receivedUpdates")
            receivedUpdates++
            sharedAtom.value = false
        }

        for (i in 0..count) {
            delay(1)
            sharedAtom.value = true
        }

        // delay enough time to receive all updates
        delay(count.toLong())

        // +2 constant for the following:
        // - initial selector get()
        // - value set to false -> selector invalidation
        // +count for the for loop invocations
        // +1 constant at the end
        // - value set to false at the end of the loop
        assertEquals(2 + count + 1, receivedUpdates)
        // all values are received by the selector because the value is
        // inverted, the repeated value check then does not ignore the update
        // even though the selector re-evaluation happens AFTER
        // the value has been reset to true.
    }

    @Test
    fun testWithScope() {
        val testCoroutineScope = TestCoroutineScope()

        val scope1 = RekoilScope(testCoroutineScope)
        val scope2 = RekoilScope(testCoroutineScope)

        val firstEmissionDelay = 10L

        lateinit var sharedAtom: Atom<Int>

        scope1.launch {
            sharedAtom = atom { 0 }

            val timesOneSelector = selector {
                get(sharedAtom) * 1
            }

            // first emission is missed
            delay(firstEmissionDelay)

            var updateCount = 0
            timesOneSelector.subscribe {
                val expectedValue: Any = when(updateCount) {
                    // result of initial value 0 is missed due to emission delay.
                    0 -> 1
                    1 -> 2
                    else -> fail("Unexpected update.")
                }
                updateCount++
                assertEquals(expectedValue, it)
            }
        }

        scope2.launch {

            val timesTwoSelector = withScope(scope1) {
                get(sharedAtom) * 2
            }

            var updateCount = 0
            timesTwoSelector.subscribe {
                val expectedValue: Any = when(updateCount) {
                    0 -> 0
                    1 -> 2
                    2 -> 4
                    else -> fail("Unexpected update.")
                }
                updateCount++
                assertEquals(expectedValue, it)
            }
        }

        val scope3 = RekoilScope(testCoroutineScope)
        scope3.launch {
            var errorCount = 0
            try {
                val badUsage = selector {
                    val result = get(sharedAtom)
                }
            } catch (expected: RekoilDependencyException) {
                // expected failure for incorrect usage out of scope.
                errorCount++
            }

            assertEquals(1, errorCount)
        }

        testCoroutineScope.runBlockingTest {
            sharedAtom.value = 1
            delay(firstEmissionDelay)
            sharedAtom.value = 2
            delay(20)
        }

    }
}