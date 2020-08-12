package com.aallam.permissionsflow

import com.aallam.permissionsflow.internal.combineGranted
import com.aallam.permissionsflow.internal.combineName
import com.aallam.permissionsflow.internal.combineShouldShowRequestPermissionRationale

/**
 * Permission request result object.
 */
public class Permission internal constructor(
    val name: String,
    val granted: Boolean,
    val shouldShowRequestPermissionRationale: Boolean = false
) {

    public constructor(permissions: List<Permission>) : this(
        name = permissions.combineName(),
        granted = permissions.combineGranted(),
        shouldShowRequestPermissionRationale = permissions.combineShouldShowRequestPermissionRationale()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Permission
        if (name != other.name) return false
        if (granted != other.granted) return false
        if (shouldShowRequestPermissionRationale != other.shouldShowRequestPermissionRationale) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + granted.hashCode()
        result = 31 * result + shouldShowRequestPermissionRationale.hashCode()
        return result
    }

    override fun toString(): String {
        return "Permission(name='$name', granted=$granted, shouldShowRequestPermissionRationale=$shouldShowRequestPermissionRationale)"
    }
}

