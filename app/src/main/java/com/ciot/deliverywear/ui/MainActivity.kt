package com.ciot.deliverywear.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.GsonUtils
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.bean.NavPointResponse
import com.ciot.deliverywear.bean.RobotAllResponse
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.constant.HttpConstant
import com.ciot.deliverywear.databinding.ActivityMainBinding
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.dialog.LoadingDialog
import com.ciot.deliverywear.ui.fragment.BindingFragment
import com.ciot.deliverywear.ui.fragment.FragmentFactory
import com.ciot.deliverywear.ui.fragment.GatewayFragment
import com.ciot.deliverywear.ui.fragment.HeadingFragment
import com.ciot.deliverywear.ui.fragment.HomeFragment
import com.ciot.deliverywear.ui.fragment.PointFragment
import com.ciot.deliverywear.ui.fragment.SettingFragment
import com.ciot.deliverywear.ui.fragment.StandbyFragment
import com.ciot.deliverywear.ui.fragment.WelcomeFragment
import com.ciot.deliverywear.utils.ContextUtil
import com.ciot.deliverywear.utils.MyDeviceUtils
import com.ciot.deliverywear.utils.PrefManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() , View.OnClickListener {
    private var mCompositeDisposable: CompositeDisposable? = null
    private var prefManager: PrefManager? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun onUnsubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }

    private fun addSubscription(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        disposable?.let { mCompositeDisposable!!.add(disposable) }
    }

    fun setBindInfo(key: String, server: String) {
        Log.d(TAG, "setBindInfo key: $key")
        prefManager?.bindKey = key
        prefManager?.isBound = true
        prefManager?.isFirstTimeLaunch = false
        prefManager?.bindServer = server
    }

    private lateinit var binding: ActivityMainBinding
    private var timeTextView: TextView? = null
    private var returnView: ImageView? = null
    private var cancelView: ImageView? = null
    private var myRobotText: TextView? = null
    private var currentfragment: BaseFragment? = null
    private var showingFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity onCreate start")
        prefManager = PrefManager(this)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initData()
        initWatch()
        getCurTime()
    }

    override fun onResume() {
        super.onResume()
        initListener()
        if (prefManager?.isFirstTimeLaunch == true) {
            showWelcome()
        }
    }

    override fun onPause() {
        super.onPause()
        FragmentFactory.clearCache()
    }

    private fun initData() {
        if (prefManager?.bindServer.isNullOrEmpty()) {
            prefManager?.bindServer = HttpConstant.DEFAULT_SERVICE_URL
        }
        RetrofitManager.instance.setDefaultServer(prefManager?.bindServer.toString())
    }

    private fun showStandby() {
        if (currentfragment is StandbyFragment) {
            return
        }
        Log.d(TAG, "MainActivity showStandby >>>>>>>>>")
        val dealResult = DealResult()
        dealResult.type = ConstantLogic.MSG_TYPE_STANDBY
        updateFragment(ConstantLogic.MSG_TYPE_STANDBY, dealResult)
    }

    fun showHome() {
        if (currentfragment is HomeFragment) {
            return
        }
        //showLoadingDialog()
        Log.d(TAG, "MainActivity showHome >>>>>>>>>")
        RetrofitManager.instance.getRobotsForHome()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object:Observer<RobotAllResponse>{
                override fun onSubscribe(d: Disposable) {
                    addSubscription(d)
                }

                override fun onNext(body: RobotAllResponse) {
                    Log.d(TAG, "RobotAllResponse: " + GsonUtils.toJson(body))
                    RetrofitManager.instance.parseRobotAllResponseBody(body)
                }

                override fun onError(e: Throwable) {
                    Log.w(TAG,"onError: ${e.message}")
                }

                override fun onComplete() {
                    val dealResult = DealResult()
                    dealResult.type = ConstantLogic.MSG_TYPE_HOME
                    dealResult.robotInfoList = RetrofitManager.instance.getRobotData()
                    Log.d(TAG, "showHome dealResult: " + GsonUtils.toJson(dealResult))
                    //dismissLoadingDialog()
                    updateFragment(ConstantLogic.MSG_TYPE_HOME, dealResult)
                    resetTimer()
                }
            })
    }

    fun showNav(robotId: String) {
        RetrofitManager.instance.getNavPoint(robotId)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<NavPointResponse> {
                override fun onSubscribe(d: Disposable) {
                    addSubscription(d)
                }

                override fun onNext(response: NavPointResponse) {
                    Log.d(TAG, "NavPointResponse: " + GsonUtils.toJson(response))
                    RetrofitManager.instance.parsePointAllResponseBody(response)
                    val dealResult = DealResult()
                    dealResult.pointInfoList = RetrofitManager.instance.getPoints()
                    dealResult.selectRobotId = robotId
                    updateFragment(ConstantLogic.MSG_TYPE_POINT, dealResult)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "点位获取失败 ：${e.message}")
                }

                override fun onComplete() {

                }
            })
    }

    private fun showWelcome() {
        Log.d(TAG, "MainActivity showWelcome >>>>>>>>>")
        updateFragment(ConstantLogic.MSG_TYPE_WELCOME, null)
    }

    private fun showSetting() {
        Log.d(TAG, "MainActivity showSetting >>>>>>>>>")
        updateFragment(ConstantLogic.MSG_TYPE_SETTING, null)
    }

    // 加载动画弹窗
    private var mLoadingDialog: LoadingDialog? = null
    private fun showLoadingDialog() {
        if (mLoadingDialog?.dialog?.isShowing != true) {
            mLoadingDialog = LoadingDialog()
            mLoadingDialog?.show(supportFragmentManager, "ScanLoadingDialog")
            Log.d(TAG,"showLoadingDialog>>>>>>")
        }
    }
    private fun dismissLoadingDialog() {
        if (mLoadingDialog?.dialog?.isShowing == true) {
            mLoadingDialog?.dismiss()
        }
    }

    private fun initListener() {
        Log.d(TAG, "initListener start")
        returnView?.setOnClickListener(this)
        cancelView?.setOnClickListener(this)
    }

    private fun initWatch() {
        Log.d(TAG, "initWatch start")
        val mac = MyDeviceUtils.getMacAddress()
        RetrofitManager.instance.setWuHanUserName(mac)
        val code = prefManager?.bindKey
        if (prefManager?.isBound == true && code != null) {
            //showLoadingDialog()
            RetrofitManager.instance.setWuHanPassWord(code)
            RetrofitManager.instance.firstLogin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onSubscribe(d: Disposable) {
                        addSubscription(d)
                    }

                    override fun onNext(body: ResponseBody) {
                        RetrofitManager.instance.parseLoginResponseBody(body)
                    }

                    override fun onError(e: Throwable) {
                        Log.w(TAG,"initWatch onError: ${e.message}")
                    }

                    override fun onComplete() {
                        showHome()
                    }
                })
        }
    }

    private fun initView() {
        Log.d(TAG, "initView start")
        timeTextView = findViewById(R.id.timeTextView)
        returnView = findViewById(R.id.return_img)
        cancelView = findViewById(R.id.cancel_img)
        myRobotText = findViewById(R.id.my_robot)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.return_img -> {
                if (currentfragment is PointFragment || currentfragment is SettingFragment) {
                    showHome()
                } else if (currentfragment is BindingFragment) {
                    if (prefManager?.isFirstTimeLaunch == true) {
                        showWelcome()
                    } else {
                        showSetting()
                    }
                } else if (currentfragment is GatewayFragment) {
                    showSetting()
                }
            }
            R.id.cancel_img -> {
                if (currentfragment is HeadingFragment) {
                    showHome()
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 捕捉所有触摸事件
        if (!(currentfragment is WelcomeFragment || currentfragment is BindingFragment)) {
            resetTimer()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun resetTimer() {
        // 重置计时器
        handler.removeCallbacks(standbyRunnable)
        handler.postDelayed(standbyRunnable, delayMillis)
    }
    private val handler = Handler(Looper.getMainLooper())
    private val standbyRunnable = Runnable {
        if (currentfragment !is SettingFragment &&
            currentfragment !is BindingFragment &&
            currentfragment !is GatewayFragment
            ) {
            // 进入待机页面
            showStandby()
        }
    }
    private val delayMillis: Long = 30000 // 30秒

    private lateinit var curTimeHandler: Handler
    private lateinit var updateTimeRunnable: Runnable
    private fun getCurTime() {
        curTimeHandler = Handler()
        updateTimeRunnable = object : Runnable {
            override fun run() {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.headView.timeTextView.text = timeFormat.format(Date())
                curTimeHandler.postDelayed(this, 1000) // 每秒更新一次时间
            }
        }
        curTimeHandler.post(updateTimeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        ContextUtil.clearContext()
        FragmentFactory.clearCache()
        onUnsubscribe()
        handler.removeCallbacks(standbyRunnable)
        curTimeHandler.removeCallbacksAndMessages(null)
        dismissLoadingDialog()
    }

    fun updateFragment(type: Int, result: DealResult?) {
        getFragment(type)
        changeFragment(type, currentfragment)
        setHeadView(type)
        if (result != null) {
            currentfragment?.refreshData(false, result)
        }
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

        val containerView: ViewGroup
        if (type == ConstantLogic.MSG_TYPE_HOME
            || type == ConstantLogic.MSG_TYPE_AREA
            || type == ConstantLogic.MSG_TYPE_POINT
            || type == ConstantLogic.MSG_TYPE_BIND
            || type == ConstantLogic.MSG_TYPE_SETTING
            || type == ConstantLogic.MSG_TYPE_GATEWAY
            || type == ConstantLogic.MSG_TYPE_HEADING
            || type == ConstantLogic.MSG_TYPE_WELCOME
            ) {
            binding.containerMain.visibility = View.VISIBLE
            containerView = binding.containerMain
            binding.containerFull.visibility = View.GONE
        } else {
            binding.containerFull.visibility = View.VISIBLE
            containerView = binding.containerFull
            binding.containerMain.visibility = View.GONE
        }
        FragmentFactory.changeFragment(supportFragmentManager, containerView, newFragment)
        showingFragment = newFragment
    }

    private fun setHeadView(type: Int) {
        when (type) {
            ConstantLogic.MSG_TYPE_HOME -> {
                timeTextView?.visibility = View.VISIBLE
                myRobotText?.visibility = View.VISIBLE
                returnView?.visibility = View.GONE
                cancelView?.visibility = View.GONE
            }
            ConstantLogic.MSG_TYPE_POINT,
            ConstantLogic.MSG_TYPE_BIND,
            ConstantLogic.MSG_TYPE_SETTING,
            ConstantLogic.MSG_TYPE_GATEWAY-> {
                timeTextView?.visibility = View.VISIBLE
                myRobotText?.visibility = View.GONE
                returnView?.visibility = View.VISIBLE
                cancelView?.visibility = View.GONE
            }
            ConstantLogic.MSG_TYPE_STANDBY -> {
                timeTextView?.visibility = View.GONE
                myRobotText?.visibility = View.GONE
                returnView?.visibility = View.GONE
                cancelView?.visibility = View.GONE
            }
            ConstantLogic.MSG_TYPE_WELCOME -> {
                timeTextView?.visibility = View.VISIBLE
                myRobotText?.visibility = View.GONE
                returnView?.visibility = View.GONE
                cancelView?.visibility = View.GONE
            }
            ConstantLogic.MSG_TYPE_HEADING -> {
                timeTextView?.visibility = View.VISIBLE
                myRobotText?.visibility = View.GONE
                returnView?.visibility = View.GONE
                cancelView?.visibility = View.VISIBLE
            }
        }
    }
}
