package com.ciot.deliverywear.ui.fragment
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ciot.deliverywear.R
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.utils.MyLog

object FragmentFactory {
    private var mCacheFragment: MutableMap<Int, BaseFragment>? = HashMap()
    private var mCurrentFragmentType = -1
    private var isClearCache: Boolean = false
    private var tag = "FragmentFactory"
    fun createFragment(fragmentType: Int): BaseFragment {
        var fragment: BaseFragment? = null
        MyLog.i(
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
            ConstantLogic.MSG_TYPE_STANDBY -> StandbyFragment()
            ConstantLogic.MSG_TYPE_BIND -> BindingFragment()
            ConstantLogic.MSG_TYPE_HOME -> HomeFragment()
            ConstantLogic.MSG_TYPE_POINT -> PointFragment()
            ConstantLogic.MSG_TYPE_HEADING -> HeadingFragment()
            ConstantLogic.MSG_TYPE_SETTING -> SettingFragment()
            ConstantLogic.MSG_TYPE_GATEWAY -> GatewayFragment()
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
            MyLog.d(ConstantLogic.FRAGMENT, "Fragment clearCache")
        }
        isClearCache = true
        mCurrentFragmentType = -1
    }

    fun release() {
        clearCache()
        MyLog.d(ConstantLogic.FRAGMENT, "Fragment release")
    }

    fun changeFragment(manager: FragmentManager, container: ViewGroup, newFragment: Fragment) {
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
            MyLog.d(tag, "changeFragment err: $e")
        }
    }
}