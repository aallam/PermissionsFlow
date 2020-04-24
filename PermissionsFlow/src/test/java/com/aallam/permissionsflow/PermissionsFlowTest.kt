package com.aallam.permissionsflow

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aallam.permissionsflow.extension.*
import com.aallam.permissionsflow.helper.FragmentController
import com.aallam.permissionsflow.internal.PERMISSIONS_REQUEST_CODE
import com.aallam.permissionsflow.internal.PermissionsDataFlow
import com.aallam.permissionsflow.internal.ShadowFragment
import com.aallam.permissionsflow.internal.reactive.BehaviorSubject
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.M])
class PermissionsFlowTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var permissionsFlow: PermissionsFlow
    private lateinit var shadowFragment: ShadowFragment

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val fragmentController = FragmentController(ShadowFragment::class.java)
        shadowFragment = spyk(fragmentController.resume())
        permissionsFlow = spyk(PermissionsDataFlow(shadowFragment))
    }

    @Test
    fun subscription_preM() = coroutineRule.runBlocking {
        val permission: String = Manifest.permission.READ_PHONE_STATE
        every { permissionsFlow.isGranted(permission) } returns true

        permissionsFlow.request(permission)
            .test()
            .assertNoErrors()
            .assertSize(1)
            .assertValues(true)
    }

    @Test
    fun subscription_granted() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns false
        simulateRequest(permission to PERMISSION_GRANTED)

        permissionsFlow.request(permission)
            .test()
            .assertNoErrors()
            .assertValues(true)
    }

    @Test
    fun eachSubscription_granted() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns false
        simulateRequest(permission to PERMISSION_GRANTED)

        permissionsFlow.requestEach(permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, true))
    }

    @Test
    fun eachSubscriptionCombined_granted() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns false
        simulateRequest(permission to PERMISSION_GRANTED)

        permissionsFlow.requestEachCombined(permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, true))
    }

    @Test
    fun eachSubscription_preM() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns true

        permissionsFlow.requestEach(permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, true))
    }

    @Test
    fun eachSubscriptionCombined_preM() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns true

        permissionsFlow.requestEachCombined(permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, true))
    }

    @Test
    fun subscription_alreadyGranted() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns true

        permissionsFlow.request(permission)
            .test()
            .assertNoErrors()
            .assertValues(true)
    }

    @Test
    fun subscription_denied() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns false
        simulateRequest(permission to PERMISSION_DENIED)

        permissionsFlow.request(permission)
            .test()
            .assertNoErrors()
            .assertValues(false)
    }

    @Test
    fun eachSubscriptionCombined_denied() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns false
        simulateRequest(permission to PERMISSION_DENIED)

        permissionsFlow.requestEachCombined(permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, false))
    }

    @Test
    fun subscription_revoked() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isRevoked(permission) } returns true

        permissionsFlow.request(permission)
            .test()
            .assertNoErrors()
            .assertValues(false)
    }

    @Test
    fun eachSubscription_revoked() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isRevoked(permission) } returns true

        permissionsFlow.requestEach(permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, false))
    }

    @Test
    fun eachSubscriptionCombined_revoked() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isRevoked(permission) } returns true

        permissionsFlow.requestEachCombined(permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, false))
    }

    @Test
    fun subscription_severalPermissions_granted() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(any()) } returns false
        simulateRequest(*permissions.resultTo(PERMISSION_GRANTED))

        permissionsFlow.request(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(true)
    }

    @Test
    fun eachSubscription_severalPermissions_granted() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(any()) } returns false
        simulateRequest(*permissions.resultTo(PERMISSION_GRANTED))

        permissionsFlow.requestEach(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permissions[0], true), Permission(permissions[1], true))
    }

    @Test
    fun eachSubscriptionCombined_severalPermissions_granted() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(any()) } returns false
        simulateRequest(*permissions.resultTo(PERMISSION_GRANTED))

        permissionsFlow.requestEachCombined(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permissions[0] + ", " + permissions[1], true))
    }

    @Test
    fun subscription_severalPermissions_oneDenied() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(any()) } returns false
        simulateRequest(permissions[0] to PERMISSION_GRANTED, permissions[1] to PERMISSION_DENIED)

        permissionsFlow.request(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(false)
    }

    @Test
    fun subscription_severalPermissions_oneRevoked() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(permissions[0]) } returns false
        every { permissionsFlow.isRevoked(permissions[1]) } returns true
        simulateRequest(permissions[0] to PERMISSION_GRANTED)

        permissionsFlow.request(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(false)
    }

    @Test
    fun eachSubscription_severalPermissions_oneAlreadyGranted() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(permissions[0]) } returns false
        every { permissionsFlow.isGranted(permissions[1]) } returns true
        val slotPermissions = simulateRequest(permissions[0] to PERMISSION_GRANTED)

        permissionsFlow.requestEach(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permissions[0], true), Permission(permissions[1], true))

        assertEquals(1, slotPermissions.captured.size)
        assertEquals(permissions[0], slotPermissions.captured[0])
    }

    @Test
    fun eachSubscriptionCombined_severalPermissions_oneAlreadyGranted() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(permissions[0]) } returns false
        every { permissionsFlow.isGranted(permissions[1]) } returns true
        val slotPermissions = simulateRequest(permissions[0] to PERMISSION_GRANTED)

        permissionsFlow.requestEachCombined(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permissions[0] + ", " + permissions[1], true))

        assertEquals(1, slotPermissions.captured.size)
        assertEquals(permissions[0], slotPermissions.captured[0])
    }

    @Test
    fun eachSubscription_severalPermissions_oneDenied() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(any()) } returns false
        simulateRequest(
            permissions[0] to PERMISSION_GRANTED,
            permissions[1] to PERMISSION_DENIED
        )

        permissionsFlow.requestEach(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permissions[0], true), Permission(permissions[1], false))
    }

    @Test
    fun eachSubscriptionCombined_severalPermissions_oneDenied() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(any()) } returns false
        simulateRequest(
            permissions[0] to PERMISSION_GRANTED,
            permissions[1] to PERMISSION_DENIED
        )

        permissionsFlow.requestEachCombined(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permissions[0] + ", " + permissions[1], false))
    }

    @Test
    fun eachSubscription_severalPermissions_oneRevoked() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(any()) } returns false
        every { permissionsFlow.isRevoked(permissions[1]) } returns true
        simulateRequest(permissions[0] to PERMISSION_GRANTED)

        permissionsFlow.requestEach(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permissions[0], true), Permission(permissions[1], false))
    }

    @Test
    fun eachSubscriptionCombined_severalPermissions_oneRevoked() = coroutineRule.runBlocking {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)

        every { permissionsFlow.isGranted(any()) } returns false
        every { permissionsFlow.isRevoked(permissions[1]) } returns true
        simulateRequest(permissions[0] to PERMISSION_GRANTED)

        permissionsFlow.requestEachCombined(*permissions)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permissions[0] + ", " + permissions[1], false))
    }

    @Test
    fun subscription_trigger_granted() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns false
        simulateRequest(permission to PERMISSION_GRANTED)

        val subject = BehaviorSubject(1).also { it.close() }
        subject
            .request(permissionsFlow, permission)
            .test()
            .assertNoErrors()
            .assertValues(true)
    }

    @Test
    fun eachSubscription_trigger_granted() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns false
        simulateRequest(permission to PERMISSION_GRANTED)

        val subject = BehaviorSubject(1).also { it.close() }
        subject
            .requestEach(permissionsFlow, permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, true))
    }


    @Test
    fun eachSubscriptionCombined_trigger_granted() = coroutineRule.runBlocking {
        val permission = Manifest.permission.READ_PHONE_STATE

        every { permissionsFlow.isGranted(permission) } returns false
        simulateRequest(permission to PERMISSION_GRANTED)

        val subject = BehaviorSubject(1).also { it.close() }
        subject
            .requestEachCombined(permissionsFlow, permission)
            .test()
            .assertNoErrors()
            .assertValues(Permission(permission, true))
    }

    @Test
    fun shouldShowRequestPermissionRationale_allDenied_allRationale() = coroutineRule.runBlocking {
        val activity = mockk<Activity>()

        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true
        every { activity.shouldShowRequestPermissionRationale(any()) } returns true

        permissionsFlow.shouldShowRequestPermissionRationale(activity, "p1", "p2")
            .test()
            .assertNoErrors()
            .assertValues(true)
    }

    @Test
    fun shouldShowRequestPermissionRationale_allDenied_oneRationale() = coroutineRule.runBlocking {
        val activity = mockk<Activity>(relaxed = true)

        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true
        every { activity.shouldShowRequestPermissionRationale("p1") } returns true

        permissionsFlow.shouldShowRequestPermissionRationale(activity, "p1", "p2")
            .test()
            .assertNoErrors()
            .assertValues(false)
    }

    @Test
    fun shouldShowRequestPermissionRationale_allDenied_noRationale() = coroutineRule.runBlocking {
        val activity = mockk<Activity>(relaxed = true)

        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true

        permissionsFlow.shouldShowRequestPermissionRationale(activity, "p1", "p2")
            .test()
            .assertNoErrors()
            .assertValues(false)
    }

    @Test
    fun shouldShowRequestPermissionRationale_oneDeniedRationale() = coroutineRule.runBlocking {
        val activity = mockk<Activity>(relaxed = true)

        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true

        permissionsFlow.shouldShowRequestPermissionRationale(activity, "p1", "p2")
            .test()
            .assertNoErrors()
            .assertValues(false)
    }

    @Test
    fun shouldShowRequestPermissionRationale_oneDeniedNotRationale() = coroutineRule.runBlocking {
        val activity = mockk<Activity>(relaxed = true)

        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true
        every { permissionsFlow.isGranted("p2") } returns true

        permissionsFlow.shouldShowRequestPermissionRationale(activity, "p1", "p2")
            .test()
            .assertNoErrors()
            .assertValues(false)
    }

    @Test
    fun isGranted_preMarshmallow() {
        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns false

        val granted = permissionsFlow.isGranted("p")

        assertTrue(granted)
    }

    @Test
    fun isGranted_granted() {
        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true
        every { shadowFragment.isGranted("p") } returns true

        val granted = permissionsFlow.isGranted("p")

        assertTrue(granted)
    }

    @Test
    fun isGranted_denied() {
        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true
        every { shadowFragment.isGranted("p") } returns false

        val granted = permissionsFlow.isGranted("p")

        assertFalse(granted)
    }

    @Test
    fun isRevoked_preMarshmallow() {
        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns false

        val granted = permissionsFlow.isRevoked("p")

        assertFalse(granted)
    }

    @Test
    fun isRevoked_true() {
        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true
        every { shadowFragment.isRevoked("p") } returns true

        val granted = permissionsFlow.isRevoked("p")

        assertTrue(granted)
    }

    @Test
    fun isRevoked_false() {
        every { (permissionsFlow as PermissionsDataFlow).isMarshmallow() } returns true
        every { shadowFragment.isRevoked("p") } returns false

        val granted = permissionsFlow.isRevoked("p")

        assertFalse(granted)
    }

    private fun simulateRequest(vararg elements: Pair<String, Int>): CapturingSlot<Array<String>> {
        val permissions = elements.toMap()
        val permissionsKeys = permissions.keys.toTypedArray()
        val grantResults = permissions.values.toIntArray()
        val slotPermissions = slot<Array<String>>()
        every { shadowFragment.requestPermissions(capture(slotPermissions), PERMISSIONS_REQUEST_CODE) } answers {
            shadowFragment.onRequestPermissionsResult(PERMISSIONS_REQUEST_CODE, permissionsKeys, grantResults)
        }
        return slotPermissions
    }

    private fun Array<String>.resultTo(permission: Int): Array<Pair<String, Int>> =
        map { it to permission }.toTypedArray()
}