package com.aallam.permissionsflow.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlin.coroutines.coroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertNull

suspend fun <T> Flow<T>.test(): TestFlow<T> {
    return coroutineScope {
        TestFlow(this, this@test)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TestFlow<T>(
    scope: CoroutineScope,
    flow: Flow<T>
) {
    private val results = mutableListOf<T>()
    private var throwable: Throwable? = null
    private val job: Job = flow
        .catch { throwable = it }
        .onCompletion {  }
        .onEach { results.add(it) }
        .launchIn(scope)

    fun assertNoValues(): TestFlow<T> {
        assertEquals(emptyList<T>(), results)
        return this
    }

    suspend fun assertValues(vararg values: T): TestFlow<T> {
        job.join()
        assertEquals(values.toList(), results)
        return this
    }

    suspend fun assertSize(size: Int): TestFlow<T> {
        job.join()
        assertEquals(size, results.size)
        return this
    }

    suspend fun assertNoErrors(): TestFlow<T> {
        job.join()
        assertNull(throwable, "No errors expected, however the following error found: $throwable")
        return this
    }

    fun finish() {
        job.cancel()
    }
}