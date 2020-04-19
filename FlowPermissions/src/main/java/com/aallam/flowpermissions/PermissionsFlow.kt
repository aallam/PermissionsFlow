package com.aallam.flowpermissions

import android.annotation.TargetApi
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.aallam.flowpermissions.internal.extension.toFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*


private const val TAG = "FlowPermissions"
private val TRIGGER = Any()

@Suppress("unused")
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FlowPermissions(activity: FragmentActivity) {

    private val permissionsFlowFragment: PermissionsFlowFragment = activity.permissionsFlowFragment

    private val FragmentActivity.permissionsFlowFragment: PermissionsFlowFragment
        get() = findPermissionsFlowFragment(this) ?: PermissionsFlowFragment().also {
            supportFragmentManager.beginTransaction().add(it, TAG).commitAllowingStateLoss()
            supportFragmentManager.executePendingTransactions()
        }

    private fun findPermissionsFlowFragment(activity: FragmentActivity): PermissionsFlowFragment? {
        return activity.supportFragmentManager.findFragmentByTag(TAG) as? PermissionsFlowFragment
    }

    public fun logging(logging: Boolean) {
        permissionsFlowFragment.logging(logging)
    }

    public fun <T> Flow<T>.ensure(vararg permissions: String): Flow<Boolean> {
        return request(this, *permissions)
            .bufferList()
            .flatMapConcat { permissionsList ->
                if (permissions.isEmpty()) {
                    // Occurs during orientation change, when the subject receives onComplete.
                    // In that case we don't want to propagate that empty list.
                    return@flatMapConcat emptyFlow<Boolean>()
                }
                val allGranted = permissionsList.all { it.granted }
                return@flatMapConcat flowOf(allGranted)
            }
    }

    /**
     * Map emitted items from the source flow into [Permission] objects for each permission in parameters.
     *
     * If one or several permissions have never been requested, invoke the related framework method to ask the user
     * if he allows the permissions.
     */
    public fun <T> Flow<T>.ensureEach(vararg permissions: String): Flow<Permission> {
        return request(this, *permissions)
    }

    /**
     * Map emitted items from the source observable into one combined [Permission] object. Only if all permissions are granted,
     * permission also will be granted. If any permission has `shouldShowRationale` checked, than result also has it checked.
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    public fun <T> Flow<T>.ensureEachCombined(vararg permissions: String): Flow<Permission> {
        return request(this, *permissions)
            .bufferList()
            .flatMapConcat {
                if (permissions.isEmpty()) emptyFlow() else flowOf(Permission(it))
            }
    }

    /**
     * Buffer all the results into a [List].
     */
    private fun <T> Flow<T>.bufferList(): Flow<List<T>> {
        return flow {
            val list = mutableListOf<T>()
            toList(list)
            emit(list)
        }
    }

    /**
     * Request permissions immediately, must be invoked during initialization phase of the application.
     */
    public fun request(vararg permissions: String): Flow<Boolean> {
        return flowOf(TRIGGER).ensure(*permissions)
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    public fun requestEach(vararg permissions: String): Flow<Permission> {
        return flowOf(TRIGGER).ensureEach(*permissions)
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    public fun requestEachCombined(vararg permissions: String): Flow<Permission> {
        return flowOf(TRIGGER).ensureEachCombined(*permissions)
    }

    private fun request(trigger: Flow<*>, vararg permissions: String): Flow<Permission> {
        require(permissions.isNotEmpty()) { "FlowPermissions.request requires at least one input permission" }
        return oneOf(trigger, pending(*permissions)).flatMapConcat {
            requestImplementation(*permissions)
        }
    }

    private fun oneOf(trigger: Flow<*>?, pending: Flow<*>): Flow<*> {
        return if (trigger == null) flowOf(TRIGGER) else merge(trigger, pending)
    }

    private fun pending(vararg permissions: String): Flow<*> {
        for (p in permissions) {
            if (!permissionsFlowFragment.containsByPermission(p)) return emptyFlow<Any>()
        }
        return flowOf(TRIGGER)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestImplementation(vararg permissions: String): Flow<Permission> {
        val list: MutableList<Flow<Permission>> = mutableListOf()
        val unrequestedPermissions: MutableList<String> = mutableListOf()

        // In case of multiple permissions, we create a Flow for each of them.
        // At the end, the flows are combined to have a unique response.
        for (permission in permissions) {
            permissionsFlowFragment.log("Requesting permission $permission")
            if (isGranted(permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                val permissionFlow = Permission(
                    name = permission,
                    granted = true,
                    shouldShowRequestPermissionRationale = false
                ).toFlow()
                list.add(permissionFlow)
                continue
            }
            if (isRevoked(permission)) {
                // Revoked by a policy, return a denied Permission object.
                val permissionFlow = Permission(
                    name = permission,
                    granted = false,
                    shouldShowRequestPermissionRationale = false
                ).toFlow()
                list.add(permissionFlow)
                continue
            }

            val subject: PublishSubject<Permission> =
                permissionsFlowFragment.getSubjectByPermission(permission) ?: PublishSubject<Permission>()
                    .also {
                        unrequestedPermissions.add(permission)
                        permissionsFlowFragment.setSubjectForPermission(permission, it)
                    }
            list.add(subject)
        }

        if (unrequestedPermissions.isNotEmpty()) {
            requestPermissionsFromFragment(unrequestedPermissions.toTypedArray())
        }

        return list.asFlow().flattenConcat()
    }

    /**
     * Returns true if the permission is already granted.
     *
     * Always true if SDK < 23.
     */
    public fun isGranted(permission: String): Boolean {
        return !isMarshmallow() || permissionsFlowFragment.isGranted(permission)
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     *
     * Always false if SDK < 23.
     */
    public fun isRevoked(permission: String): Boolean {
        return isMarshmallow() && permissionsFlowFragment.isRevoked(permission)
    }

    private fun isMarshmallow(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    @TargetApi(Build.VERSION_CODES.M)
    public fun requestPermissionsFromFragment(permissions: Array<String>) {
        permissionsFlowFragment.log("requestPermissionsFromFragment ${permissions.joinToString()}")
        permissionsFlowFragment.requestPermissions(permissions)
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps the returned value in an flow.
     *
     * In case of multiple permissions, only emits true if Activity.shouldShowRequestPermissionRationale returned true
     * for all revoked permissions.
     *
     * You shouldn't call this method if all permissions have been granted.
     *
     * For SDK < 23, the flow will always emit false.
     */
    public fun shouldShowRequestPermissionRationale(
        activity: FragmentActivity,
        vararg permissions: String
    ): Flow<Boolean> {
        if (isMarshmallow()) {
            return flowOf(shouldShowRequestPermissionRationaleImplementation(activity, *permissions))
        }
        return flowOf(false)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun shouldShowRequestPermissionRationaleImplementation(
        activity: FragmentActivity,
        vararg permissions: String
    ): Boolean {
        return permissions.all { isGranted(it) || activity.shouldShowRequestPermissionRationale(it) }
    }

}
