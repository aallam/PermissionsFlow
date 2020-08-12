package com.aallam.permissionsflow.internal

import com.aallam.permissionsflow.Permission

internal fun List<Permission>.combineName(): String = map(Permission::name).joinToString()

internal fun List<Permission>.combineGranted(): Boolean = all(Permission::granted)

internal fun List<Permission>.combineShouldShowRequestPermissionRationale(): Boolean =
    any(Permission::shouldShowRequestPermissionRationale)