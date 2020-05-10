package com.aallam.permissionsflow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test

class Flow {

    @Test
    fun test(): Unit = runBlockingTest {
        val state = MutableStateFlow<String?>(null)

        val flow = state.filterNotNull()
        val flowOk = flowOf("ok")
        val list = listOf(flow, flowOk)

        val job = list.merge()
                .onEach { println(it) }
                .launchIn(this)

        state.value = "String0"
        state.value = "String1"
        //yield()
        state.value = "String2"
        //yield()
        delay(100)
        job.cancel()
    }
}