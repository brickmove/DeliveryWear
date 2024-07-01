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
            ConstantLogic.MSG_TYPE_HOME -> HomeFragment()
            ConstantLogic.MSG_TYPE_POINT -> PointFragment()
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

    fun changeFragment1(manager: FragmentManager, container: ViewGroup, newFragment: Fragment) {
        Log.d(tag, "changeFragment>>>>>>>>")
        try {
            val transaction = manager.beginTransaction()

            // 遍历当前已添加的Fragment，隐藏除了newFragment之外的所有BaseFragment
            manager.fragments.forEach {
                if (it is BaseFragment && it !== newFragment) {
                    transaction.hide(it)
                }
            }

            // 判断newFragment是否已经存在于FragmentManager中
            var isNew = true
            for (fragment in manager.fragments) {
                if (fragment === newFragment) {
                    isNew = false
                    break
                }
            }

            // 如果newFragment是新的实例，则添加到容器并加入到回退堆栈
            if (isNew) {
                transaction.add(container.id, newFragment)
                // 将事务添加到回退堆栈，使得这个Fragment可以被回退
                transaction.addToBackStack(null)
            } else {
                // 如果newFragment已经存在，则显示它
                transaction.show(newFragment)
            }

            // 设置转场动画
            transaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out)
            // 提交事务，允许在状态丢失时提交，以避免异常
            transaction.commitNowAllowingStateLoss()

        } catch (e: Exception) {
            Log.d(tag, "changeFragment err: ", e)
        }
    }

}