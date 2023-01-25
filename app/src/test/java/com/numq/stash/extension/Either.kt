package com.numq.stash.extension

import arrow.core.right
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext

internal class Either {

    internal class Sync {
        @Test
        fun `get exception if thrown`() {
            val e = Exception("fatal exception")
            catch<Unit> { throw e }.run {
                assertTrue(isLeft())
                tapLeft {
                    assertEquals(it.message, e.message)
                }
            }
        }

        @Test
        fun `get result of proper calculation`() {
            catch { 2 * 2 }.run {
                assertTrue(isRight())
                assertEquals(this, 4.right())
            }
        }

        @Test
        fun `get argument exception condition is false`() {
            val e = Exception("condition exception")
            catch(false, e) { true }.run {
                assertTrue(isLeft())
                tapLeft {
                    println(it)
                    assertEquals(it.message, e.message)
                }
            }
        }

        @Test
        fun `get result if condition is true`() {
            catch(true, Exception()) { 0 }.run {
                assertTrue(isRight())
                assertEquals(this, 0.right())
            }
        }


    }

    internal class Async {

        private lateinit var coroutineContext: CoroutineContext

        @BeforeEach
        fun `setup context`() {
            coroutineContext = Job()
        }

        @Test
        fun `get exception if thrown`() = runBlocking {
            val e = Exception("fatal exception")
            catchAsync<Unit>(coroutineContext) { throw e }.run {
                assertTrue(isLeft())
                tapLeft {
                    assertEquals(it.message, e.message)
                }
            }
        }

        @Test
        fun `get result of proper calculation`() = runBlocking {
            catchAsync(coroutineContext) { 2 * 2 }.run {
                assertTrue(isRight())
                assertEquals(this, 4.right())
            }
        }

        @Test
        fun `get argument exception condition is false`() = runBlocking {
            val e = Exception("condition exception")
            catchAsync(false, e, coroutineContext) { true }.run {
                assertTrue(isLeft())
                tapLeft {
                    assertEquals(it.message, e.message)
                }
            }
        }

        @Test
        fun `get result if condition is true`() = runBlocking {
            catchAsync(true, Exception(), coroutineContext) { 0 }.run {
                assertTrue(isRight())
                assertEquals(this, 0.right())
            }
        }
    }
}