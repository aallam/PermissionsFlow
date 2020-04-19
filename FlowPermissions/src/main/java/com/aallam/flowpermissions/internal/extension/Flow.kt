package com.aallam.flowpermissions.internal.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal fun <T> T.toFlow(): Flow<T> = flowOf(this)
