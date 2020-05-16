package com.aallam.permissionsflow

/**
 * Permission request result object.
 */
class Permission internal constructor(
    val name: String,
    val granted: Boolean,
    val shouldShowRequestPermissionRationale: Boolean = false
) {

    constructor(permissions: List<Permission>) : this(
        name = combineName(permissions),
        granted = combineGranted(permissions),
        shouldShowRequestPermissionRationale = combineShouldShowRequestPermissionRationale(permissions)
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

private fun combineName(permissions: List<Permission>): String = permissions.map(Permission::name).joinToString()

private fun combineGranted(permissions: List<Permission>): Boolean = permissions.all(Permission::granted)

private fun combineShouldShowRequestPermissionRationale(permissions: List<Permission>): Boolean =
    permissions.any(Permission::shouldShowRequestPermissionRationale)
