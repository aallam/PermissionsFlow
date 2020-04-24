package com.aallam.permissionsflow.internal

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aallam.permissionsflow.Permission
import com.aallam.permissionsflow.internal.reactive.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal const val PERMISSIONS_REQUEST_CODE = 42
private const val TAG = "PermissionsFlowFragment"

internal class ShadowFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private val subjects: MutableMap<String, BehaviorSubject<Permission>> = mutableMapOf()
    private var logging: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun requestPermissions(permissions: Array<String>) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSIONS_REQUEST_CODE) return

        val shouldShowRequestPermissionRationale: BooleanArray = permissions
            .map(this::shouldShowRequestPermissionRationale)
            .toBooleanArray()
        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale)

    }

    internal fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray,
        shouldShowRequestPermissionRationale: BooleanArray
    ) {
        launch {
            permissions.forEachIndexed { index, permission ->
                log("onRequestPermissionsResult: $permission")
                subjects[permission]?.let { subject ->
                    subjects.remove(permission)
                    subject.use {
                        val granted = grantResults[index] == PackageManager.PERMISSION_GRANTED
                        it.emit(Permission(permissions[index], granted, shouldShowRequestPermissionRationale[index]))
                    }
                } ?: Log.e(TAG, "onRequestPermissionsResult: no corresponding permission request found")
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    public fun isRevoked(permission: String): Boolean {
        return requireActivity().packageManager.isPermissionRevokedByPolicy(permission, requireActivity().packageName)
    }

    public fun getSubjectByPermission(permission: String): BehaviorSubject<Permission>? {
        return subjects[permission]
    }

    public fun containsByPermission(permission: String): Boolean {
        return subjects.containsKey(permission)
    }

    public fun setSubjectForPermission(
        permission: String,
        subject: BehaviorSubject<Permission>
    ): BehaviorSubject<Permission>? {
        return subjects.put(permission, subject)
    }

    public fun logging(logging: Boolean) {
        this.logging = logging
    }

    internal fun log(message: String) {
        if (logging) Log.d(TAG, message)
    }
}