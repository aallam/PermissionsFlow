package com.aallam.permissionsflow.helper

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import org.robolectric.android.controller.ComponentController

/** Version of FragmentController that can be used for androidx.fragment.app.Fragment.  */
class SupportFragmentController<F : Fragment>(
    private val fragment: F,
    activityClass: Class<out FragmentActivity>?,
    intent: Intent? = null
) : ComponentController<SupportFragmentController<F>, F>(fragment, intent) {

    private val activityController: ActivityController<out FragmentActivity> =
        Robolectric.buildActivity(activityClass, intent)

    /**
     * Creates the activity with [Bundle] and adds the fragment to the view with ID
     * `contentViewId`.
     */
    fun create(contentViewId: Int, bundle: Bundle?): SupportFragmentController<F> {
        shadowMainLooper.runPaused {
            activityController
                .create(bundle)
                .get()
                .supportFragmentManager
                .beginTransaction()
                .add(contentViewId, fragment)
                .commitNow()
        }
        return this
    }

    /**
     * Creates the activity with [Bundle] and adds the fragment to it. Note that the fragment
     * will be added to the view with ID 1.
     */
    fun create(bundle: Bundle?): SupportFragmentController<F> {
        return create(1, bundle)
    }

    override fun create(): SupportFragmentController<F> {
        return create(null)
    }

    override fun destroy(): SupportFragmentController<F> {
        shadowMainLooper.runPaused { activityController.destroy() }
        return this
    }

    fun start(): SupportFragmentController<F> {
        shadowMainLooper.runPaused { activityController.start() }
        return this
    }

    fun resume(): SupportFragmentController<F> {
        shadowMainLooper.runPaused { activityController.resume() }
        return this
    }

    fun pause(): SupportFragmentController<F> {
        shadowMainLooper.runPaused { activityController.pause() }
        return this
    }

    fun stop(): SupportFragmentController<F> {
        shadowMainLooper.runPaused { activityController.stop() }
        return this
    }

    fun visible(): SupportFragmentController<F> {
        shadowMainLooper.runPaused { activityController.visible() }
        return this
    }

    private class FragmentControllerActivity : FragmentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val view = LinearLayout(this)
            view.id = 1
            setContentView(view)
        }
    }

    companion object {
        fun <F : Fragment> of(fragment: F): SupportFragmentController<F> {
            return SupportFragmentController(
                fragment,
                FragmentControllerActivity::class.java
            )
        }

        fun <F : Fragment> of(fragment: F, activityClass: Class<out FragmentActivity>): SupportFragmentController<F> {
            return SupportFragmentController(fragment, activityClass)
        }

        fun <F : Fragment> of(
            fragment: F,
            activityClass: Class<out FragmentActivity>,
            intent: Intent?
        ): SupportFragmentController<F> {
            return SupportFragmentController(fragment, activityClass, intent)
        }

        /**
         * Sets up the given fragment by attaching it to an activity, calling its onCreate() through
         * onResume() lifecycle methods, and then making it visible. Note that the fragment will be added
         * to the view with ID 1.
         */
        fun <F : Fragment> setupFragment(fragment: F): F {
            return of(fragment).create().start().resume().visible().get()
        }

        /**
         * Sets up the given fragment by attaching it to an activity, calling its onCreate() through
         * onResume() lifecycle methods, and then making it visible. Note that the fragment will be added
         * to the view with ID 1.
         */
        fun <F : Fragment> setupFragment(
            fragment: F, fragmentActivityClass: Class<out FragmentActivity>
        ): F {
            return of(fragment, fragmentActivityClass)
                .create()
                .start()
                .resume()
                .visible()
                .get()
        }

        /**
         * Sets up the given fragment by attaching it to an activity created with the given bundle,
         * calling its onCreate() through onResume() lifecycle methods, and then making it visible. Note
         * that the fragment will be added to the view with ID 1.
         */
        fun <F : Fragment> setupFragment(
            fragment: F, fragmentActivityClass: Class<out FragmentActivity>, bundle: Bundle?
        ): F {
            return of(fragment, fragmentActivityClass)
                .create(bundle)
                .start()
                .resume()
                .visible()
                .get()
        }

        /**
         * Sets up the given fragment by attaching it to an activity created with the given bundle and
         * container id, calling its onCreate() through onResume() lifecycle methods, and then making it
         * visible.
         */
        fun <F : Fragment> setupFragment(
            fragment: F,
            fragmentActivityClass: Class<out FragmentActivity>,
            containerViewId: Int,
            bundle: Bundle?
        ): F {
            return of(fragment, fragmentActivityClass)
                .create(containerViewId, bundle)
                .start()
                .resume()
                .visible()
                .get()
        }
    }

}