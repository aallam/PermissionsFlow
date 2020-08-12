package com.aallam.permissionsflow.internal.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

internal const val TAG = "FlowPermissions"

/**
 * Equivalent to `flowOf(value)`
 */
internal fun <T> T.toFlow(): Flow<T> = flowOf(this)

/**
 * Buffer all the results into a [List].
 */
internal fun <T> Flow<T>.bufferList(size: Int): Flow<List<T>> {
    return flow {
        var list: MutableList<T> = mutableListOf()
        collect { value ->
            list.let {
                it.add(value)
                if (it.size == size) {
                    emit(it)
                    list = mutableListOf() // prepare next list buffer
                }
            }
        }
    }
}
