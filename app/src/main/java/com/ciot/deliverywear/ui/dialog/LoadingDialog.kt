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

class LoadingDialog : DialogFragment() {
    private var enterHint: TextView? = null
    private var countdownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, container, false)
        initView(view)
        return view
    }

    override fun onStart() {
        dialog?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        //hideBottomUIMenu()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#CC000000")))
        }
        super.onStart()
    }

    private fun initView(view: View?) {
        enterHint = view?.findViewById(R.id.enter_project_in)
    }

    override fun dismiss() {
        super.dismiss()
        countdownTimer?.cancel()
    }
}