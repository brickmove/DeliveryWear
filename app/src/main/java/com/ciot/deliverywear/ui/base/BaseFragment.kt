package com.ciot.deliverywear.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.util.LinkedList
import com.ciot.deliverywear.bean.DealResult

open class BaseFragment : Fragment() {
    private var mRootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mRootView == null) {
            mRootView = createView(inflater, container)
        }
        return mRootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
    }

    open fun createView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        return null
    }

    /**
     * 初始化控件
     * @param savedInstanceState Bundle对象，存储页面异常关闭时的状态
     */
    open fun initView(savedInstanceState: Bundle?) {

    }

    /**
     * @param isRefreshImmediately 如果Fragment已经创建过，传递true，否则传递false
     * @param data
     */
    open fun refreshData(isRefreshImmediately: Boolean, data: DealResult) {

    }
}