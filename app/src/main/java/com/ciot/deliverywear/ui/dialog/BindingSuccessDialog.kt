package com.ciot.deliverywear.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ciot.deliverywear.R
import com.ciot.deliverywear.ui.MainActivity

class BindingSuccessDialog : DialogFragment() {
    private var enterHint: TextView? = null
    private var countdownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_binding_success, container, false)
        initView(view)
        return view
    }

    override fun onStart() {
        hideBottomUIMenu()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#CC000000")))
        }
        super.onStart()
        startCountdown()
    }

    private fun hideBottomUIMenu() {
        val decorView: View = dialog!!.window!!.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.INVISIBLE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
    }

    private fun initView(view: View?) {
        enterHint = view?.findViewById(R.id.enter_project_in)
    }

    private fun startCountdown() {
        val countDownInterval = 1000L
        val totalDuration = 4000L

        countdownTimer = object : CountDownTimer(totalDuration, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                enterHint?.text = "Entering Project In $secondsRemaining"
            }

            override fun onFinish() {
                (activity as MainActivity).showHome()
                dismiss()
            }
        }.start()
    }

    override fun dismiss() {
        super.dismiss()
        countdownTimer?.cancel()
        enterHint?.text = getString(R.string.entering_project)
    }
}