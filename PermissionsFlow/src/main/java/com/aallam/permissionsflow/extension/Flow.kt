package com.aallam.permissionsflow.extension

import androidx.fragment.app.FragmentActivity
import com.aallam.permissionsflow.Permission
import com.aallam.permissionsflow.PermissionsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Map emitted items from the source flow into true if permissions in parameters are granted, or false if not.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.request(activity: FragmentActivity, vararg permissions: String): Flow<Boolean> {
    return flatMapLatest { PermissionsFlow(activity).request(*permissions) }
}

/**
 * Map emitted items from the source flow into true if permissions in parameters are granted, or false if not.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.request(permissionsFlow: PermissionsFlow, vararg permissions: String): Flow<Boolean> {
    return flatMapLatest { permissionsFlow.request(*permissions) }
}

/**
 * Map emitted items from the source flow into [Permission] objects for each permission in parameters.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.ensureEach(activity: FragmentActivity, vararg permissions: String): Flow<Permission> {
    return flatMapLatest { PermissionsFlow(activity).requestEach(*permissions) }
}

/**
 * Map emitted items from the source flow into [Permission] objects for each permission in parameters.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.requestEach(permissionsFlow: PermissionsFlow, vararg permissions: String): Flow<Permission> {
    return flatMapLatest { permissionsFlow.requestEach(*permissions) }
}

/**
 * Map emitted items from the source observable into one combined [Permission] object. Only if all permissions are granted,
 * permission also will be granted. If any permission has `shouldShowRationale` checked, than result also has it checked.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.requestEachCombined(activity: FragmentActivity, vararg permissions: String): Flow<Permission> {
    return flatMapLatest { PermissionsFlow(activity).requestEachCombined(*permissions) }
}

/**
 * Map emitted items from the source observable into one combined [Permission] object. Only if all permissions are granted,
 * permission also will be granted. If any permission has `shouldShowRationale` checked, than result also has it checked.
 *
 * If one or several permissions have never been requested, invoke the related framework method to ask for permissions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.requestEachCombined(permissionsFlow: PermissionsFlow, vararg permissions: String): Flow<Permission> {
    return flatMapLatest { permissionsFlow.requestEachCombined(*permissions) }
}
