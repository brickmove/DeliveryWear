package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.ciot.deliverywear.R
import com.ciot.deliverywear.constant.NetConstant
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.dialog.SwitchProjectDialog

class GatewayFragment : BaseFragment(), SwitchProjectDialog.DialogListener {
    companion object {
        private const val TAG = "GatewayFragment"
    }

    private var usServerText: TextView? = null
    private var usServerCard: CardView? = null
    private var hkServerText: TextView? = null
    private var hkServerCard: CardView? = null
    private var cnServerText: TextView? = null
    private var cnServerCard: CardView? = null
    private var devServerText: TextView? = null
    private var devServerCard: CardView? = null

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_gateway , container , false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        usServerText = view?.findViewById(R.id.us_server_tv)
        hkServerText = view?.findViewById(R.id.hk_server_tv)
        cnServerText = view?.findViewById(R.id.cn_server_tv)
        devServerText = view?.findViewById(R.id.dev_server_tv)

        usServerCard = view?.findViewById(R.id.us_server)
        hkServerCard = view?.findViewById(R.id.hk_server)
        cnServerCard = view?.findViewById(R.id.cn_server)
        devServerCard = view?.findViewById(R.id.dev_server)

        setDefaultColor()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        usServerCard?.setOnClickListener {
            //RetrofitManager.instance.setDefaultServer(HttpConstant.US_SERVICE_URL)
            usServerText?.let { it1 -> setTextColor(it1, R.color.yellow) }
            hkServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            cnServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            devServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            showSwitchDialog(NetConstant.US_SERVICE_URL)
        }

        hkServerCard?.setOnClickListener {
            //RetrofitManager.instance.setDefaultServer(HttpConstant.HK_SERVICE_URL)
            hkServerText?.let { it1 -> setTextColor(it1, R.color.yellow) }
            cnServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            usServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            devServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            showSwitchDialog(NetConstant.HK_SERVICE_URL)
        }

        cnServerCard?.setOnClickListener {
            //RetrofitManager.instance.setDefaultServer(HttpConstant.CN_SERVICE_URL)
            cnServerText?.let { it1 -> setTextColor(it1, R.color.yellow) }
            usServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            hkServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            devServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            showSwitchDialog(NetConstant.CN_SERVICE_URL)
        }

        devServerCard?.setOnClickListener {
            //RetrofitManager.instance.setDefaultServer(HttpConstant.CN_SERVICE_URL)
            cnServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            usServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            hkServerText?.let { it1 -> setTextColor(it1, R.color.white) }
            devServerText?.let { it1 -> setTextColor(it1, R.color.yellow) }
            showSwitchDialog(NetConstant.DEV_SERVICE_URL)
        }
    }

    private fun setTextColor(view: TextView, resId: Int) {
        view.setTextColor(ContextCompat.getColor(activity?.applicationContext!!, resId))
    }

    private var mSwitchDialog: SwitchProjectDialog? = null
    private fun showSwitchDialog(server: String) {
        if (mSwitchDialog?.dialog?.isShowing != true) {
            mSwitchDialog = SwitchProjectDialog(server)
            mSwitchDialog!!.setDialogListener(this)
            mSwitchDialog?.show(childFragmentManager, "ScanSwitchProjectDialog")
            Log.d(TAG,"showSwitchDialog>>>>>>")
        }
    }
    private fun dismissSwitchDialog() {
        if (mSwitchDialog?.dialog?.isShowing == true) {
            mSwitchDialog?.dismiss()
        }
    }

    private fun setDefaultColor() {
        cnServerText?.let { setTextColor(it, R.color.white) }
        hkServerText?.let { setTextColor(it, R.color.white) }
        usServerText?.let { setTextColor(it, R.color.white) }
        devServerText?.let { setTextColor(it, R.color.white) }
        when (RetrofitManager.instance.getDefaultServer()) {
            NetConstant.CN_SERVICE_URL -> cnServerText?.let { setTextColor(it, R.color.yellow) }
            NetConstant.HK_SERVICE_URL -> hkServerText?.let { setTextColor(it, R.color.yellow) }
            NetConstant.US_SERVICE_URL -> usServerText?.let { setTextColor(it, R.color.yellow) }
            NetConstant.DEV_SERVICE_URL -> devServerText?.let { setTextColor(it, R.color.yellow) }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            dismissSwitchDialog()
        }
        setDefaultColor()
    }

    override fun onDialogClosed() {
        setDefaultColor()
    }
}