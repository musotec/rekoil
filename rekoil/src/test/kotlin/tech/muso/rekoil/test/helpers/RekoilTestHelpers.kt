@file:Suppress("NOTHING_TO_INLINE")
package tech.muso.rekoil.test.helpers

import junit.framework.Assert.fail
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import tech.muso.rekoil.core.Atom
import tech.muso.rekoil.core.RekoilContext
import tech.muso.rekoil.core.RekoilScope
import tech.muso.rekoil.core.RekoilScopeImpl
import java.lang.AssertionError
import java.util.concurrent.atomic.AtomicInteger

/**
 * Combined TestCoroutineScope and RekoilScope for evaluation of tests.
 */
class TestRekoilScope(
        coroutineScope: TestCoroutineScope,
        val rekoilScope: RekoilScope = RekoilScopeImpl(coroutineScope)
) : RekoilScope by rekoilScope, TestCoroutineScope by coroutineScope {


}

const val ATOM_VALUE_1 = "default"
const val ATOM_VALUE_2 = "new value"
const val ATOM_VALUE_3 = "third value"

var verbose = true
inline fun <T : Any> T.assertEquals(expected: Any?, actual: Any?, message: String? = null) {
    if (verbose) {
        try {
            junit.framework.Assert.assertEquals(message, expected, actual).run { "PASSED" }
        } catch (err: AssertionError) {
            println("!! FAILED !! ${message?.let {"$it: "} ?: ""}assertEquals($expected, $actual)")
            fail(err.message)
        }

        // format success message
        println("             ${message?.let {"$it: "} ?: ""}assertEquals($expected, $actual)")
    } else {
        junit.framework.Assert.assertEquals(message, expected, actual)
    }
}

/**
 * Mock launch function that launches the test scope and exposes the TestRekoilScope.
 */
fun TestRekoilScope.launch(
    block: suspend TestRekoilScope.() -> Unit
): Job {
    // TODO: DO SOMETHING IDK
    return this.rekoilContext.coroutineScope.launch {
        block()
    }
}

// helper to evaluate within a blocking test scope and handle
// the release() call on the scope so that the indefinite jobs
// normally cancelled by the CoroutineScope do not fail the test.
inline fun rekoilScopeTest(noinline block: suspend TestRekoilScope.() -> Unit) =
    runBlockingTest {
        TestRekoilScope(coroutineScope = this).apply {
            launch(block).join()    // join to ensure all code in block executes.
            releaseScope()               // release to free any jobs left from communication channels

        }
    }

inline fun TestRekoilScope.defaultAtom(): Atom<String> {
    val testAtom = atom { "default" }
    assertEquals("default", testAtom.value, "defaultAtom(): ")
    return testAtom
}

internal fun <T> RekoilContext.ValueNode<T>.subscribeTest(
        vararg expectedValues: T,
        tag: (() -> String)? = null
): RekoilContext.ValueNode<T> {
    // synchronize updates to avoid multi thread (subscribe vs onRelease)
    val receivedUpdates = AtomicInteger(0)
    // create an option tag argument after our expected value list.
    val label = tag?.let { "[${it()}]" } ?: ""
    val expectedList = expectedValues.asList()
    subscribe {
        println("___")
        assertEquals(expectedList[receivedUpdates.getAndIncrement()], it, "$label onValue($this)")
    }   // subscribe channel may finish after onRelease() for pending message

    onRelease {
        assertEquals(expectedList.size, receivedUpdates.get(), "$label onRelease($this)")
    }

    return this
}
