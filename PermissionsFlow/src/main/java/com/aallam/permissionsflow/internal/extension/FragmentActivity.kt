package com.aallam.permissionsflow.internal.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.aallam.permissionsflow.internal.ShadowFragment

internal val FragmentActivity.permissionsFlowFragment: ShadowFragment
    get() = shadowFragment(supportFragmentManager)

internal val Fragment.permissionsFlowFragment: ShadowFragment
    get() = shadowFragment(childFragmentManager)

private fun shadowFragment(fragmentManager: FragmentManager): ShadowFragment {
    return findPermissionsFlowFragment(fragmentManager) ?: ShadowFragment().also {
        fragmentManager.beginTransaction().add(it, TAG).commitAllowingStateLoss()
        fragmentManager.executePendingTransactions()
    }
}

private fun findPermissionsFlowFragment(fragmentManager: FragmentManager): ShadowFragment? {
    return fragmentManager.findFragmentByTag(TAG) as? ShadowFragment
}