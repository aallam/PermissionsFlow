package com.aallam.permissionsflow.internal.reactive

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * Represents an object that is both an Observable [Flow] and an Observer [FlowCollector].
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class Subject<T, R>(
    private val channel: BroadcastChannel<T>
) : FlowCollector<T>, Flow<R>, AutoCloseable {

    override fun close() {
        channel.cancel()
    }
}