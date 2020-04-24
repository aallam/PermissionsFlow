package com.aallam.permissionsflow.sample

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.aallam.permissionsflow.PermissionsFlow
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// This is an arbitrary number we are using to keep track of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts.
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE)

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    private fun startCamera() {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder()
            .apply {
                setTargetResolution(Size(640, 480))
            }
            .build()

        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        try {
            CameraX.bindToLifecycle(this, preview)
        } catch (e: Exception) {
            Log.e("MainActivity", "error while CameraX binding", e)
        }
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            val permissionsFlow = PermissionsFlow.of(this)
            permissionsFlow.logging(true)
            button.setOnClickListener {
                permissionsFlow.requestEach(*REQUIRED_PERMISSIONS)
                    .onEach { permission ->
                        if (!permission.granted) {
                            Log.d("MainActivity", "permission refused: $permission")
                            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                                .show()
                            return@onEach
                        }
                        if (permission.name == Manifest.permission.CAMERA && permission.granted) {
                            viewFinder.post { startCamera() }
                        }
                        Log.d("MainActivity", "permission received: $permission")
                    }
                    .launchIn(lifecycleScope)
            }
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onStop() {
        super.onStop()
        releaseCamera()
    }

    private fun releaseCamera() {
        //if (camera != null) {
        //    camera.release()
        //    camera = null
        //}
    }
}
