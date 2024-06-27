package com.ciot.deliverywear.ui.fragment
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.R
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.base.BaseFragment

object FragmentFactory {
    private var mCacheFragment: MutableMap<Int, BaseFragment>? = HashMap()
    private var mCurrentFragmentType = -1
    private var isClearCache: Boolean = false
    private var tag = "FragmentFactory"
    fun createFragment(fragmentType: Int): BaseFragment {
        var fragment: BaseFragment? = null
        Log.i(
            ConstantLogic.FRAGMENT,
            "createFragment:$fragmentType,last fragment is:$mCurrentFragmentType"
        )
        if (mCacheFragment!!.containsKey(fragmentType)) {
            fragment = mCacheFragment!![fragmentType]
            mCurrentFragmentType = fragmentType
            if (fragment != null) {
                return fragment
            }
        }
        fragment = when (fragmentType) {
            ConstantLogic.MSG_TYPE_WELCOME -> WelcomeFragment()
            ConstantLogic.MSG_TYPE_LOGIN -> LoginFragment()
            else -> HomeFragment()
        }
        mCacheFragment!![fragmentType] = fragment
        mCurrentFragmentType = fragmentType
        return fragment
    }

    fun getFragmentFromType(type: Int): BaseFragment? {
        return if (mCacheFragment!!.containsKey(type)) {
            mCacheFragment!![type]
        } else null
    }

    fun getmCurrentFragmentType(): Int {
        return mCurrentFragmentType
    }

    fun clearCache() {
        if (mCacheFragment != null) {
            mCacheFragment!!.clear()
            Log.d(ConstantLogic.FRAGMENT, "Fragment clearCache")
        }
        isClearCache = true
        mCurrentFragmentType = -1
    }

    fun release() {
        clearCache()
        Log.d(ConstantLogic.FRAGMENT, "Fragment release")
    }

    fun changeFragment(manager: FragmentManager, container: ViewGroup, newFragment: Fragment) {
        Log.d(tag, "changeFragment>>>>>>>>")
        try {
            var isNew = true
            val transaction = manager.beginTransaction()
            manager.fragments.map {
                if (it == null) return@map
                if (it == newFragment) {
                    isNew = false
                    transaction.show(it)
                } else if (it is BaseFragment) {
                    transaction.hide(it)
                }
            }
            if (isNew) {
                transaction.add(container.id, newFragment)
            }
            transaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out)
            transaction.commitNowAllowingStateLoss()

        } catch (e: Exception) {
            Log.d(tag, "changeFragment err: ", e)
        }
    }
}