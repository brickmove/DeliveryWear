package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealReault
import com.ciot.deliverywear.ui.base.BaseFragment
import java.util.LinkedList

class HomeFragment: BaseFragment() {
    private var mData : LinkedList<out DealReault>? = null
    private var mDealResult : DealReault? = null

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_home , container , false)
        if (mData != null) {
            mData = null
        }
        return view
    }
}