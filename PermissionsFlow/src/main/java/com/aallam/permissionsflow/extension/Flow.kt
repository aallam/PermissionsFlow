package com.aallam.permissionsflow.extension

import androidx.fragment.app.FragmentActivity
import com.aallam.permissionsflow.Permission
import com.aallam.permissionsflow.PermissionsFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat

/**
 * Map emitted items from the source flow into true if permissions in parameters are granted, or false if not.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(FlowPreview::class)
fun <T> Flow<T>.request(activity: FragmentActivity, vararg permissions: String): Flow<Boolean> {
    return flatMapConcat { PermissionsFlow.of(activity).request(*permissions) }
}

fun <T> Flow<T>.request(permissionsFlow: PermissionsFlow, vararg permissions: String): Flow<Boolean> {
    return flatMapConcat { permissionsFlow.request(*permissions) }
}

/**
 * Map emitted items from the source flow into [Permission] objects for each permission in parameters.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(FlowPreview::class)
fun <T> Flow<T>.ensureEach(activity: FragmentActivity, vararg permissions: String): Flow<Permission> {
    return flatMapConcat { PermissionsFlow.of(activity).requestEach(*permissions) }
}

fun <T> Flow<T>.requestEach(permissionsFlow: PermissionsFlow, vararg permissions: String): Flow<Permission> {
    return flatMapConcat { permissionsFlow.requestEach(*permissions) }
}

/**
 * Map emitted items from the source observable into one combined [Permission] object. Only if all permissions are granted,
 * permission also will be granted. If any permission has `shouldShowRationale` checked, than result also has it checked.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(FlowPreview::class)
fun <T> Flow<T>.requestEachCombined(activity: FragmentActivity, vararg permissions: String): Flow<Permission> {
    return flatMapConcat { PermissionsFlow.of(activity).requestEachCombined(*permissions) }
}

fun <T> Flow<T>.requestEachCombined(permissionsFlow: PermissionsFlow, vararg permissions: String): Flow<Permission> {
    return flatMapConcat { permissionsFlow.requestEachCombined(*permissions) }
}
