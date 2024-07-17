package com.ciot.deliverywear.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.utils.VibrationUtils

class HeadingFragment : BaseFragment() {
    private var headingText: TextView? = null
    private var headingItemText: TextView? = null
    private var headingCancel: Button? = null
    private var countdownTimer: CountDownTimer? = null
    private lateinit var localRobot: String
    private var isVibrate: Boolean = false

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_heading, container, false)
        return view
    }

    private fun startCountdown() {
        // 设置倒计时5秒
        val countDownInterval = 1000L // 每秒减少一次
        val totalDuration = 5000L // 总时长为5秒
        if (isVibrate) {
            VibrationUtils.vibrate(requireContext(), 5000) // 震动5s
        }
        countdownTimer = object : CountDownTimer(totalDuration, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                // 更新界面显示剩余时间
                val secondsRemaining = millisUntilFinished / 1000
                headingItemText?.text = "Close Page In $secondsRemaining"
            }

            override fun onFinish() {
                // 倒计时结束返回首页
                (activity as MainActivity).showHome()
                VibrationUtils.cancelVibration(requireContext())
            }
        }.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initListener()
        startCountdown()
    }

    private fun initListener() {
        // 点击取消后返回上一页
        headingCancel?.setOnClickListener{
            if (localRobot.isEmpty()) {
                (activity as MainActivity).showHome()
            } else {
                (activity as MainActivity).showNav(localRobot)
            }
        }
    }

    private fun initView(view: View?) {
        headingText = view?.findViewById(R.id.heading_tx)
        headingItemText = view?.findViewById(R.id.heading_item)
        headingCancel = view?.findViewById(R.id.heading_cancel)
    }

    override fun refreshData(isRefreshImmediately: Boolean, data: DealResult) {
        if (data.selectPoint.isNullOrEmpty()) {
            return
        }
        localRobot = data.selectRobotId.toString()
        val navInfo = data.navInfo
        headingText?.text = navInfo + data.selectPoint
        isVibrate = data.isVibratorOn
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            countdownTimer?.cancel()
            VibrationUtils.cancelVibration(requireContext())
        } else {
            countdownTimer?.start()
        }
    }
}