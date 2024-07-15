package com.ciot.deliverywear.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.MainActivity

class SwitchProjectDialog(private val server: String) : DialogFragment() {
    private var yesTv: TextView? = null
    private var noTv: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_switch_project, container, false)
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
        initListener()
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

    private fun initListener() {
        yesTv?.setOnClickListener{
            RetrofitManager.instance.setDefaultServer(server)
            (activity as MainActivity).updateFragment(ConstantLogic.MSG_TYPE_BIND, DealResult())
            dismiss()
        }

        noTv?.setOnClickListener{
            dismiss()
        }
    }

    private fun initView(view: View?) {
        yesTv = view?.findViewById(R.id.switch_yes)
        noTv = view?.findViewById(R.id.switch_no)
    }

    private var dialogListener: DialogListener? = null

    fun setDialogListener(listener: DialogListener) {
        dialogListener = listener
    }

    override fun dismiss() {
        super.dismiss()
        dialogListener?.onDialogClosed()
    }

    interface DialogListener {
        fun onDialogClosed()
    }
}