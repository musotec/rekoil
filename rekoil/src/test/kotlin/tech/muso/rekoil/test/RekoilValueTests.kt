package tech.muso.rekoil.test

import junit.framework.Assert.fail
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncompletedCoroutinesError
import org.junit.Test
import tech.muso.rekoil.core.Atom
import tech.muso.rekoil.core.async
import tech.muso.rekoil.core.launch
import tech.muso.rekoil.core.rekoilScope
import tech.muso.rekoil.test.helpers.*

@ExperimentalCoroutinesApi
class RekoilValueTests {

    @Test
    fun testAtomValue() = rekoilScopeTest {
        val testAtom = defaultAtom()
    }

    @Test
    fun testAtomValueMutationDirect() = rekoilScopeTest {
        val testAtom = defaultAtom()
        testAtom.value = ATOM_VALUE_2
        assertEquals(ATOM_VALUE_2, testAtom.value)
    }

    @Test
    fun testAtomValueMutationIndirect() = rekoilScopeTest {
        defaultAtom().subscribeTest(ATOM_VALUE_1, ATOM_VALUE_2).value = ATOM_VALUE_2
    }

    @Test
    fun testSelectorScopeGet() = rekoilScopeTest {
        val testAtom = defaultAtom()
        val testSelector = selector {
            assertEquals(ATOM_VALUE_1, get(testAtom))
        }
    }

    @Test
    fun testSelectorTransformsAtomValue() = rekoilScopeTest {
        val testAtom = defaultAtom()
        val testSelector = selector {
            get(testAtom).length
        }

        assertEquals(ATOM_VALUE_1.length, testSelector.value)
    }

    @Test
    fun testSelectorReceivesAtomUpdates() = rekoilScopeTest {
        val testAtom = defaultAtom()
        val testSelector = selector {
            get(testAtom).length
        }
        testSelector.subscribeTest(ATOM_VALUE_1.length, ATOM_VALUE_2.length)
        testAtom.value = ATOM_VALUE_2
    }

    @Test
    fun testSelectorScopeSuspension() = rekoilScopeTest {
        val testAtom = defaultAtom()
        val testSelectorHitBoth = selector {
            assertEquals(0L, currentTime % 2, "Time Start")
            delay(1)    // delay less than we wait before sending new value
            assertEquals(1L, currentTime % 2, "Time PreGet")
            get(testAtom).length
        }

        delay(2)    // delay past time to get 1st/default
        assertEquals(2L, currentTime, "Time Set 2nd")   // assert time
        testSelectorHitBoth.subscribeTest(ATOM_VALUE_1.length, ATOM_VALUE_2.length)
        testAtom.value = ATOM_VALUE_2

        // without 1 step delay at end, the testSelector will not go past the delay to hit both.
        advanceTimeBy(1)
    }

    @Test
    fun testSelectorScopeSuspensionSkipsFirstEmit() = rekoilScopeTest {
        val testAtom = defaultAtom()
        val testSelectorSkipFirst = selector {
            assertEquals(0L, currentTime)
            delay(2)    // delay longer than we wait
            assertEquals(2L, currentTime)
            get(testAtom).length
        }

        // only delay 1 second to emit new value.
        delay(1)
        assertEquals(1L, currentTime)
        testSelectorSkipFirst.subscribeTest(ATOM_VALUE_2.length)
        testAtom.value = ATOM_VALUE_2

        // allow for 2nd to be processed
        advanceTimeBy(1)
    }


    @Test
    fun testSelectorScopeSuspensionMissesEmit() = try {
        rekoilScopeTest {
            val testAtom = defaultAtom()
            val testSelectorSkipFirst = selector {
                assertEquals(0L, currentTime)
                delay(2)    // delay longer than we wait
                assertEquals(2L, currentTime)
                get(testAtom).length
            }

            // only delay 1 second to emit new value.
            delay(1)
            assertEquals(1L, currentTime)
            testSelectorSkipFirst.subscribeTest(ATOM_VALUE_2.length)
            testAtom.value = ATOM_VALUE_2

            // do not allow time for 2nd to be processed
        }

        fail("RekoilScope did not exit while having coroutines active for processing the 2nd Atom value.")
    } catch (expected: UncompletedCoroutinesError) {
        // expected error (test passes)
    }

    @Test
    fun testMultiLayerSelectorsReceiveUpdates() = rekoilScopeTest {
        val testAtom = defaultAtom()
        val testSelectorBottleneck = selector {
            get(testAtom).length
        }
        val testSelector1 = selector {
            get(testSelectorBottleneck)
        }
        val testSelector2 = selector {
            get(testSelectorBottleneck)
        }

        testSelector1.subscribeTest(ATOM_VALUE_1.length, ATOM_VALUE_2.length, ATOM_VALUE_3.length)
        testSelector2.subscribeTest(ATOM_VALUE_1.length, ATOM_VALUE_2.length, ATOM_VALUE_3.length)

        testAtom.value = ATOM_VALUE_2
        testAtom.value = ATOM_VALUE_3
    }

    @Test
    fun testRekoilSendsUpdatesSelectorToSelector() = rekoilScopeTest {
        val delay = 10L

        val testAtom = defaultAtom()
        val testSelectorWithDelay = selector {
            assertEquals(0L, (currentTime % delay), "Selector Start on Frame")
            delay(delay)   // computing for 10 seconds after testAtom updated
            assertEquals(1L, ((currentTime + 1) % delay), "Selector End on Frame")
            get(testAtom).length
        }

        val testSelectorOfSelector = selector {
            val result = get(testSelectorWithDelay)?.let{ it * it } ?: 0
            result * 2
        }

        assertEquals(0L, currentTime, "Ensure Time")

        testAtom.subscribeTest(
                ATOM_VALUE_1,
                ATOM_VALUE_2,
                ATOM_VALUE_3,
                ATOM_VALUE_1
        ) {
            "A1"
        }// all values

        testSelectorWithDelay.subscribeTest(
                // expect that we miss the first & third value due to the delay
                ATOM_VALUE_2.length,
                ATOM_VALUE_1.length    // 9 & 7 respectively
        ) {
            "S1"
        }

        // second layer selector test.
        testSelectorOfSelector.subscribeTest(
                ATOM_VALUE_2.length.let { it * it * 2 },
                ATOM_VALUE_1.length.let { it * it * 2 }
        ) {
            "S2"
        }

        testAtom.value = ATOM_VALUE_2

        val timeJustBeforeRead = delay - 1

        delay(timeJustBeforeRead)    // delay 9, expecting first get(testAtom) at time 10
        assertEquals(timeJustBeforeRead, currentTime, "Time After 2nd Post")

        delay(1)    // delay 1, expecting get(testAtom) at same time as post of new value.

        testAtom.value = ATOM_VALUE_3

        delay(timeJustBeforeRead)   // delay 9, to just before get(atom)
        assertEquals(delay * 2 - 1, currentTime, "Time After 3rd Post")
//        delay(0)    // delay 0 (post before read)
        testAtom.value = ATOM_VALUE_1

        delay(delay)
    }

    @Test
    fun testManyComplexDependencyGraphTasks() {
        (0..1000).forEach {
            testRekoilSendsUpdatesSelectorToSelector()
        }   // TODO: verify these all run.
    }

    suspend fun main() = rekoilScope {
        val atom1 = atom { "Hello" }.apply {
            subscribe { println(it) }
        }

        atom1.value = "World"
    }

    @Test
    fun testReadmeFunction() {
        runBlocking {
            main()
        }
    }

    @Test
    fun testOutOfScopeSubscription() {
        var atom1: Atom<Int>? = null
        GlobalScope.launch {
            rekoilScope {
                atom1 = atom { 0 }
                delay(10000)
            }

        }

        // subscribe first
        atom1?.subscribe {
            println("received: $it")
        }

        // send new values
        for (i in 1..5) {
            atom1?.value = i
        }
    }
}