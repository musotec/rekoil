package tech.muso.rekoil.test

import junit.framework.TestCase.fail
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import tech.muso.rekoil.core.Atom
import tech.muso.rekoil.core.RekoilScope
import tech.muso.rekoil.ktx.*
import tech.muso.rekoil.test.helpers.assertEquals

class RekoilKtxTests {

    @Test
    fun testDelegatePropertySet() {

        val testScope = RekoilScope(TestCoroutineScope())
        val testAtom: Atom<Int> = testScope.atom { 1 }

        var atomDelegateProperty: Int by testAtom

        var updateNumber = 0
        testScope.selector {
            val atomValue = get(testAtom)
            val expectedReturnValue: Any = when(updateNumber) {
                0 -> 1
                1 -> 2
                else -> fail()
            }
            assertEquals(expectedReturnValue, atomValue)
            updateNumber++
        }

        // note: lint reports unused assignment, but property is read by selector
        atomDelegateProperty = 2
    }

    @Test
    fun testDelegatePropertyGet() {

        val testScope = RekoilScope(TestCoroutineScope())
        val testAtom: Atom<Int> = testScope.atom { 1 }

        var atomDelegateProperty: Int by testAtom

        var updateNumber = 0
        val testSelector = testScope.selector {
            val atomValue = get(testAtom)
            val expectedReturnValue: Any = when(updateNumber) {
                0 -> 1
                1 -> 2
                else -> fail()
            }
            assertEquals(expectedReturnValue, atomValue)
            updateNumber++
        }

        testScope.selector {
            get(testSelector)
            val expectedReturnValue: Any = when(updateNumber) {
                // zero is missed because we increment in first selector.
                1 -> 1
                2 -> 2
                else -> fail()
            }
            assertEquals(expectedReturnValue, atomDelegateProperty)
        }

        atomDelegateProperty = 2
    }


}