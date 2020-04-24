package com.aallam.permissionsflow.reactive

import com.aallam.permissionsflow.internal.reactive.Subject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class PublishSubject<T>(
    private val channel: BroadcastChannel<T> = BroadcastChannel(Channel.BUFFERED)
) : Subject<T, T>(channel) {

    override suspend fun emit(value: T) {
        channel.send(value)
    }

    override suspend fun collect(collector: FlowCollector<T>) {
        collector.emitAll(channel.openSubscription())
    }
}