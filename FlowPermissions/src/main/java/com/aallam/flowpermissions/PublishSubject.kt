package com.aallam.flowpermissions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class PublishSubject<T>(
    private val channel: BroadcastChannel<T> = BroadcastChannel(BUFFERED)
) : FlowCollector<T>, Flow<T> by channel.asFlow() {

    override suspend fun emit(value: T) {
        channel.send(value)
    }
}