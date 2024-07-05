package com.ciot.deliverywear.ui.dialog

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
        dialog?.window?.apply {
            setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
        super.onStart()
        initListener()
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
}