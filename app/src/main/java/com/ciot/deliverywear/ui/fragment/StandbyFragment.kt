package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StandbyFragment : BaseFragment() {
    private var timeStandby: TextView? = null
    private var dateStandby: TextView? = null
    private lateinit var sleepHandler: Handler
    private lateinit var sleepRunnable: Runnable

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_standby, container , false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            (activity as? MainActivity)?.showHome()
            true
        }
    }

    private fun initView(view: View?) {
        timeStandby = view?.findViewById(R.id.standby_time)
        dateStandby = view?.findViewById(R.id.standby_date)
        getCurTime()
    }

    private fun getCurTime() {
        sleepHandler = Handler()
        sleepRunnable = object : Runnable {
            override fun run() {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
                timeStandby?.text = timeFormat.format(Date())
                dateStandby?.text = dateFormat.format(Date())
                sleepHandler.postDelayed(this, 60000L) // 每60秒更新一次时间
            }
        }
        sleepHandler.post(sleepRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sleepHandler.removeCallbacksAndMessages(null)
    }
}