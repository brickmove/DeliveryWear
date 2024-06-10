package com.ciot.deliverywear.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.utils.ContextUtil
import com.ciot.deliverywear.utils.PrefManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity(), View.OnClickListener {
    private var timeTextView: TextView? = null
    private var welcomeSmile: ImageView? = null
    private var welcomeWords: TextView? = null
    private var enterPassword: CardView? = null

    // 连续点击次数
    val mCounts: Int = 8
    //连续点击有效时间
    val mDuration: Long = 2 * 1000

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
        val prefManager = PrefManager(this)
        if (prefManager.isFirstTimeLaunch) {
            setContentView(R.layout.fragment_welcome)
            prefManager.isFirstTimeLaunch = false
        } else {
            if (prefManager.isBound) {
                setContentView(R.layout.fragment_home)
            } else {
                setContentView(R.layout.fragment_welcome)
            }
        }
        initView()
        getCurTime()
    }

    private fun initView() {
        timeTextView = findViewById(R.id.timeTextView)
        welcomeSmile = findViewById(R.id.welcome_smile)
        welcomeWords = findViewById(R.id.welcome_words)
        enterPassword = findViewById(R.id.enter_password)
        enterPassword?.setOnClickListener(this)
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.enter_password -> {
                welcomeSmile?.visibility = View.GONE
                welcomeWords?.visibility = View.GONE
                enterPassword?.visibility = View.GONE


            }
        }
    }
    private fun getCurTime() {
        val handler = Handler()

        val updateTimeRunnable = object : Runnable {
            override fun run() {
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                timeTextView?.text = dateFormat.format(Date())
                //Log.d(TAG, "getCurTime: " + timeTextView?.text)
                handler.postDelayed(this, 1000) // 每秒更新一次时间
            }
        }
        handler.post(updateTimeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        ContextUtil.clearContext()
    }
}
