package com.aallam.permissionsflow.internal.reactive

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll

/**
 * [Subject] that emits the most recent item it has observed and all subsequent observed items to each subscriber.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class, InternalCoroutinesApi::class)
internal class BehaviorSubject<T>(
    initValue: T? = null,
    private val channel: BroadcastChannel<T> = BroadcastChannel(BUFFERED)
) : Subject<T, T>(channel) {

    var current = atomic(initValue)

    override suspend fun collect(collector: FlowCollector<T>) {
        current.value?.let { collector.emit(it) }
        collector.emitAll(channel.openSubscription())
    }

    override suspend fun emit(value: T) {
        current.value = value
        if (!channel.isClosedForSend) {
            channel.send(value)
        }
    }
}
