package com.aallam.permissionsflow

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.aallam.permissionsflow.internal.PermissionsDataFlow
import com.aallam.permissionsflow.internal.PermissionsDataFlow.Companion.permissionsFlowFragment
import kotlinx.coroutines.flow.Flow

interface PermissionsFlow {

    /**
     * Request permissions immediately, must be invoked during initialization phase of the application.
     */
    fun request(vararg permissions: String): Flow<Boolean>

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    fun requestEach(vararg permissions: String): Flow<Permission>

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    fun requestEachCombined(vararg permissions: String): Flow<Permission>

    /**
     * Returns true if the permission is already granted.
     *
     * Always true if SDK < 23.
     */
    fun isGranted(permission: String): Boolean

    /**
     * Returns true if the permission has been revoked by a policy.
     *
     * Always false if SDK < 23.
     */
    fun isRevoked(permission: String): Boolean

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps the returned value in a [Flow].
     *
     * In case of multiple permissions, only emits true if Activity.shouldShowRequestPermissionRationale returned true
     * for all revoked permissions.
     *
     * You shouldn't call this method if all permissions have been granted.
     *
     * For SDK < 23, the observable will always emit false.
     */
    fun shouldShowRequestPermissionRationale(activity: Activity, vararg permissions: String): Flow<Boolean>

    /**
     * Logging setup.
     */
    fun logging(logging: Boolean)

    companion object {

        @JvmStatic
        fun of(activity: FragmentActivity): PermissionsFlow = PermissionsDataFlow(activity.permissionsFlowFragment)
    }


}