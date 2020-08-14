package com.aallam.permissionsflow

import com.aallam.permissionsflow.internal.combineGranted
import com.aallam.permissionsflow.internal.combineName
import com.aallam.permissionsflow.internal.combineShouldShowRequestPermissionRationale

/**
 * Permission request result object.
 *
 * @param name permission name
 * @param granted true if the permission is granted, false otherwise.
 * @param shouldShowRequestPermissionRationale true if should show request permission rationale, false otherwise.
 */
public class Permission internal constructor(
    val name: String,
    val granted: Boolean,
    val shouldShowRequestPermissionRationale: Boolean = false
) {

    /** @param permissions list of permission to combine. **/
    public constructor(permissions: List<Permission>) : this(
        name = permissions.combineName(),
        granted = permissions.combineGranted(),
        shouldShowRequestPermissionRationale = permissions.combineShouldShowRequestPermissionRationale()
    )

    /** @suppress **/
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Permission
        if (name != other.name) return false
        if (granted != other.granted) return false
        if (shouldShowRequestPermissionRationale != other.shouldShowRequestPermissionRationale) return false
        return true
    }

    /** @suppress **/
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + granted.hashCode()
        result = 31 * result + shouldShowRequestPermissionRationale.hashCode()
        return result
    }

    /** @suppress **/
    override fun toString(): String {
        return "Permission(name='$name', granted=$granted, shouldShowRequestPermissionRationale=$shouldShowRequestPermissionRationale)"
    }
}

