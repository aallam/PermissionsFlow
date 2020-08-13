package com.aallam.permissionsflow.internal

import com.aallam.permissionsflow.Permission

/**
 * Combine permissions by name.
 */
internal fun List<Permission>.combineName(): String = map(Permission::name).joinToString()

/**
 * Combine permissions by granted.
 */
internal fun List<Permission>.combineGranted(): Boolean = all(Permission::granted)

/**
 * Combine permissions by shouldShowRequestPermissionRationale.
 */
internal fun List<Permission>.combineShouldShowRequestPermissionRationale(): Boolean =
    any(Permission::shouldShowRequestPermissionRationale)