package com.ciot.deliverywear.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.ciot.deliverywear.R
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.utils.ContextUtil
import com.ciot.deliverywear.utils.MyDeviceUtils
import com.ciot.deliverywear.utils.PrefManager
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.fragment.FragmentFactory
import com.ciot.deliverywear.ui.fragment.WelcomeFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() , View.OnClickListener {
    private var timeTextView: TextView? = null
    private var timeStandby: TextView? = null
    private var dateStandby: TextView? = null
    private var welcomeSmile: ImageView? = null
    private var welcomeWords: TextView? = null
    private var returnView: ImageView? = null
    private var enterPassword: CardView? = null
    private var deviceImgView: ImageView? = null
    private var prefManager: PrefManager? = null
    private var currentfragment: BaseFragment? = null
    private var showingFragment: Fragment? = null
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity onCreate start")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_welcome)
        initWatch()
        initView()
        getCurTime()
        initListener()
    }

    private fun initListener() {
        enterPassword?.setOnClickListener(this)
        deviceImgView?.setOnClickListener(this)
    }

    private fun initWatch() {
        Log.d(TAG, "initWatch start")
        val mac = MyDeviceUtils.getMacAddress()
        RetrofitManager.instance.setWuHanUserName(mac)
        val code = prefManager?.bindKey
        if (prefManager?.isBound == true && code != null) {
            RetrofitManager.instance.setWuHanPassWord(code)
            RetrofitManager.instance.toLogin()
        }
    }

    private fun initView() {
        timeTextView = findViewById(R.id.timeTextView)
        timeStandby = findViewById(R.id.standby_time)
        dateStandby = findViewById(R.id.standby_date)
        welcomeSmile = findViewById(R.id.welcome_smile)
        welcomeWords = findViewById(R.id.welcome_words)
        enterPassword = findViewById(R.id.enter_password)
        deviceImgView = findViewById(R.id.device_image)
        returnView = findViewById(R.id.return_img)
        returnView?.visibility = View.INVISIBLE
//        prefManager = PrefManager(this)
//        if (prefManager!!.isFirstTimeLaunch && prefManager?.bindKey == null) {
//            setContentView(R.layout.fragment_welcome)
//            prefManager!!.isFirstTimeLaunch = false
//        } else {
//            if (prefManager!!.isBound) {
//                setContentView(R.layout.fragment_home)
//            } else {
//                setContentView(R.layout.fragment_welcome)
//            }
//        }
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.enter_password -> {
                Log.d(TAG, "---enter_password---")
                // 接口测试
                //RetrofitManager.instance.setWuHanPassWord("40399636")
                //RetrofitManager.instance.toLogin()
                updateFragment(ConstantLogic.MSG_TYPE_LOGIN)
            }
            R.id.button_got_it -> {

            }
            R.id.device_image -> {
                updateFragment(ConstantLogic.MSG_TYPE_AREA)
            }
        }
    }
    private fun getCurTime() {
        val handler = Handler()

        val updateTimeRunnable = object : Runnable {
            override fun run() {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
                timeTextView?.text = timeFormat.format(Date())
                timeStandby?.text = timeFormat.format(Date())
                dateStandby?.text = dateFormat.format(Date())
                handler.postDelayed(this, 1000) // 每秒更新一次时间
            }
        }
        handler.post(updateTimeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        ContextUtil.clearContext()
    }

    private fun updateFragment(type: Int) {
        getFragment(type)
        changeFragment(type, currentfragment)
    }

    fun getCurrentFragment(): BaseFragment? {
        return currentfragment
    }

    private fun getFragment(type: Int) {
        currentfragment = FragmentFactory.createFragment(type)
    }

    private fun changeFragment(type: Int, newFragment: Fragment?) {
        if (newFragment == null) {
            Log.d(TAG, "changeFragment current fragment == null")
            return
        }

        if (newFragment == showingFragment) {
            Log.d(TAG, "changeFragment newFragment->${newFragment.javaClass.simpleName} == showingFragment>${showingFragment?.javaClass?.simpleName}")
            return
        }
        FragmentFactory.changeFragment(supportFragmentManager, R.id.container_full, newFragment)
        showingFragment = newFragment
    }
}
