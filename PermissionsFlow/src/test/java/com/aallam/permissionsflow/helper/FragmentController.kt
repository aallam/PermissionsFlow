package com.aallam.permissionsflow.helper

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Helper class to allow starting Fragments, similar to the old SupportFragmentController.
 */
class FragmentController<T : Fragment?>(clazz: Class<T>) {

    private val scenario: FragmentScenario<T> = FragmentScenario.launch(clazz)

    fun resume(): T = runBlocking {
        fragment()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun fragment(): T = suspendCancellableCoroutine { cont ->
        scenario.onFragment { fragment ->
            cont.resume(fragment) {
                Log.e("FragmentController", "Error on fragment creation: $it")
                throw it
            }
        }
    }

    fun reset() {
        scenario.recreate()
    }
}