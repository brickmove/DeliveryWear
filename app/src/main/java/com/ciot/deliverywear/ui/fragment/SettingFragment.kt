package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.base.BaseFragment

// 初次使用绑定绑定项目密钥页面
class SettingFragment : BaseFragment() {
    companion object {
        private const val TAG = "SettingFragment"
    }

    private var settingProject: CardView? = null
    private var settingProjectName: TextView? = null
    private var settingGateway: CardView? = null
    private var settingGatewayName: TextView? = null

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_setting , container , false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        settingProject = view?.findViewById(R.id.setting_project)
        settingGateway = view?.findViewById(R.id.setting_gateway)
        settingProjectName = view?.findViewById(R.id.setting_project_name)
        settingGatewayName = view?.findViewById(R.id.setting_gateway_name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingProjectName?.text = RetrofitManager.instance.getProjectName()
        settingGatewayName?.text = (activity as MainActivity).getBindServer()
    }
}