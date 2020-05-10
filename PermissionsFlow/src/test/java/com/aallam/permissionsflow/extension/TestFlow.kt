package com.aallam.permissionsflow.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.test.assertEquals
import kotlin.test.assertNull

suspend fun <T> Flow<T>.test(): TestFlow<T> {
    return coroutineScope {
        TestFlow(this, this@test)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TestFlow<T>(scope: CoroutineScope, flow: Flow<T>) {
    private val results = mutableListOf<T>()
    private var throwable: Throwable? = null

    init {
        flow.onEach { results.add(it) }
                .catch { throwable = it }
                .launchIn(scope)
                .cancel()
    }

    fun assertNoValues(): TestFlow<T> {
        assertEquals(emptyList<T>(), results)
        return this
    }

    suspend fun assertValues(vararg values: T): TestFlow<T> {
        assertEquals(values.toList(), results)
        return this
    }

    suspend fun assertSize(size: Int): TestFlow<T> {
        assertEquals(size, results.size)
        return this
    }

    suspend fun assertNoErrors(): TestFlow<T> {
        assertNull(throwable, "No errors expected, however the following error found: $throwable")
        return this
    }
}