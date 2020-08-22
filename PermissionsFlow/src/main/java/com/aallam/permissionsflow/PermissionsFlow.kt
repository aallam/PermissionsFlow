@file:Suppress("FunctionName")

package com.aallam.permissionsflow

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.aallam.permissionsflow.internal.PermissionsDataFlow
import com.aallam.permissionsflow.internal.PermissionsDataFlow.Companion.permissionsFlowFragment
import kotlinx.coroutines.flow.Flow

/**
 * Simple static interface for permissions requests.
 */
public interface PermissionsFlow {

    /**
     * Request permissions immediately, must be invoked during initialization phase of the application.
     */
    public fun request(vararg permissions: String): Flow<Boolean>

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    public fun requestEach(vararg permissions: String): Flow<Permission>

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    public fun requestEachCombined(vararg permissions: String): Flow<Permission>

    /**
     * Returns true if the permission is already granted.
     *
     * Always true if SDK < 23.
     */
    public fun isGranted(permission: String): Boolean

    /**
     * Returns true if the permission has been revoked by a policy.
     *
     * Always false if SDK < 23.
     */
    public fun isRevoked(permission: String): Boolean

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps the returned value in a flow.
     *
     * In case of multiple permissions, only emits true if Activity.shouldShowRequestPermissionRationale returned true
     * for all revoked permissions.
     *
     * You shouldn't call this method if all permissions have been granted.
     *
     * For SDK < 23, the observable will always emit false.
     */
    public fun shouldShowRequestPermissionRationale(activity: Activity, vararg permissions: String): Flow<Boolean>

    /**
     * Logging setup.
     */
    public fun logging(logging: Boolean)
}

/**
 * Creates a [PermissionsFlow] with the specified fragment activity.
 *
 * @param activity Android Activity
 * @param logging true of logging enabled, otherwise false.
 */
public fun PermissionsFlow(activity: FragmentActivity, logging: Boolean = false): PermissionsFlow {
    return PermissionsDataFlow(activity.permissionsFlowFragment).also {
        it.logging(logging)
    }
}

/**
 * Creates a [PermissionsFlow] with the specified fragment.
 *
 * @param fragment Android Fragment
 * @param logging true of logging enabled, otherwise false.
 */
public fun PermissionsFlow(fragment: Fragment, logging: Boolean = false): PermissionsFlow {
    return PermissionsDataFlow(fragment.permissionsFlowFragment).also {
        it.logging(logging)
    }
}
