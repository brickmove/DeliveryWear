package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ciot.deliverywear.R
import java.util.LinkedList
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.ui.base.BaseFragment

// 初次使用绑定绑定项目密钥页面
class WelcomeFragment : BaseFragment() {
    private var mData : LinkedList<out DealResult>? = null
    private var mDealResult : DealResult? = null

    companion object {
        private const val TAG = "WelcomeFragment"
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_welcome , container , false)
        if (mData != null) {
            //updateData(mData !!)
            mData = null
        }
        return view
    }

}