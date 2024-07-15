package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.base.BaseFragment

class SettingFragment : BaseFragment() {
    companion object {
        private const val TAG = "SettingFragment"
    }
    //连续点击次数
    private val mCounts: Int = 8
    //连续点击有效时间
    private val mDuration: Long = 2 * 1000
    private var mProjectHits = LongArray(mCounts)
    private var mGatewayHits = LongArray(mCounts)

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
        settingGatewayName?.text = RetrofitManager.instance.getDefaultServer()
        initListener()
    }

    private fun initListener() {
        settingProject?.setOnClickListener {
            System.arraycopy(mProjectHits, 1, mProjectHits, 0, mProjectHits.size - 1)
            mProjectHits[mProjectHits.size - 1] = SystemClock.uptimeMillis()
            if (mProjectHits[0] >= (SystemClock.uptimeMillis() - mDuration)) {
                (activity as MainActivity).updateFragment(ConstantLogic.MSG_TYPE_BIND, DealResult())
            }
        }

        settingGateway?.setOnClickListener {
            System.arraycopy(mGatewayHits, 1, mGatewayHits, 0, mGatewayHits.size - 1)
            mGatewayHits[mGatewayHits.size - 1] = SystemClock.uptimeMillis()
            if (mGatewayHits[0] >= (SystemClock.uptimeMillis() - mDuration)) {
                (activity as MainActivity).updateFragment(ConstantLogic.MSG_TYPE_GATEWAY, DealResult())
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            mProjectHits = LongArray(mCounts)
            mGatewayHits = LongArray(mCounts)
        }
    }
}