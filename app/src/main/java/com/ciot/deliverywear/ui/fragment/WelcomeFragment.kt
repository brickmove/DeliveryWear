package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.base.BaseFragment

// 初次使用绑定绑定项目密钥页面
class WelcomeFragment : BaseFragment() {
    companion object {
        private const val TAG = "WelcomeFragment"
    }

    private var enterPwdButton: CardView? = null
    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_welcome , container , false)
        enterPwdButton = view.findViewById(R.id.enter_password)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterPwdButton?.setOnClickListener{
            val dealResult = DealResult()
            (activity as MainActivity).updateFragment(ConstantLogic.MSG_TYPE_BIND, dealResult)
        }
    }
}