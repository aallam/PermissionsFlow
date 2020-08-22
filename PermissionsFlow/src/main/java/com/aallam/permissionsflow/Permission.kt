@file:Suppress("FunctionName")

package com.aallam.permissionsflow

import com.aallam.permissionsflow.internal.PermissionData
import com.aallam.permissionsflow.internal.combineGranted
import com.aallam.permissionsflow.internal.combineName
import com.aallam.permissionsflow.internal.combineShouldShowRequestPermissionRationale

/**
 * Permission request result object.
 */
public interface Permission {

    /**
     * Permission name.
     */
    val name: String

    /**
     * Permission is granted or not.
     */
    val granted: Boolean

    /**
     * Should show request permission rationale or not.
     */
    val shouldShowRequestPermissionRationale: Boolean
}

/**
 * Permission request result object.
 *
 * @param name permission name
 * @param granted true if the permission is granted, false otherwise.
 * @param shouldShowRequestPermissionRationale true if should show request permission rationale, false otherwise.
 */
public fun Permission(
    name: String,
    granted: Boolean,
    shouldShowRequestPermissionRationale: Boolean = false,
): Permission = PermissionData(
    name = name,
    granted = granted,
    shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale
)

/**
 * Permission request result object.
 *
 * @param permissions list of permission to combine
 */
public fun Permission(permissions: List<Permission>): Permission = PermissionData(
    name = permissions.combineName(),
    granted = permissions.combineGranted(),
    shouldShowRequestPermissionRationale = permissions.combineShouldShowRequestPermissionRationale()
)
