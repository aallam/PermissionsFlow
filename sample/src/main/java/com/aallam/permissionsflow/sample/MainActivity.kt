package com.aallam.permissionsflow.sample

import android.Manifest.permission.*
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.aallam.permissionsflow.PermissionsFlow
import com.aallam.permissionsflow.extension.request
import com.aallam.permissionsflow.extension.requestEach
import com.aallam.permissionsflow.sample.extension.clicks
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionsFlow = PermissionsFlow(this, logging = true)

        if (permissionsFlow.isGranted(CAMERA)) cameraStatus.setGranted()
        if (permissionsFlow.isGranted(READ_CONTACTS)) contactsStatus.setGranted()
        if (permissionsFlow.isGranted(RECORD_AUDIO)) audioStatus.setGranted()

        requestCamera.clicks()
            .request(permissionsFlow, CAMERA)
            .onEach { if (it) cameraStatus.setGranted() else cameraStatus.setDenied() }
            .launchIn(lifecycleScope)

        requestContacts.clicks()
            .request(permissionsFlow, READ_CONTACTS)
            .onEach { if (it) contactsStatus.setGranted() else contactsStatus.setDenied() }
            .launchIn(lifecycleScope)

        requestContacts.setOnClickListener {

        }

        requestContacts.setOnClickListener {
            permissionsFlow.request(READ_CONTACTS)
                .onEach { if (it) contactsStatus.setGranted() else contactsStatus.setDenied() }
                .launchIn(lifecycleScope)
        }

        requestAudio.clicks()
            .request(permissionsFlow, RECORD_AUDIO)
            .onEach { if (it) audioStatus.setGranted() else audioStatus.setDenied() }
            .launchIn(lifecycleScope)

        requestAll.clicks()
            .requestEach(permissionsFlow, CAMERA, READ_CONTACTS, RECORD_AUDIO)
            .onEach {
                when (it.name) {
                    CAMERA -> if (it.granted) cameraStatus.setGranted() else cameraStatus.setDenied()
                    READ_CONTACTS -> if (it.granted) contactsStatus.setGranted() else contactsStatus.setDenied()
                    RECORD_AUDIO -> if (it.granted) audioStatus.setGranted() else audioStatus.setDenied()
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun TextView.setGranted() {
        if (visibility != View.VISIBLE) visibility = View.VISIBLE
        text = resources.getText(R.string.request_granted)
        val grantedColor = ContextCompat.getColor(context, R.color.electricBlue)
        setTextColor(grantedColor)
    }

    private fun TextView.setDenied() {
        if (visibility != View.VISIBLE) visibility = View.VISIBLE
        text = resources.getText(R.string.request_denied)
        val deniedColor = ContextCompat.getColor(context, R.color.sunglow)
        setTextColor(deniedColor)
    }
}
