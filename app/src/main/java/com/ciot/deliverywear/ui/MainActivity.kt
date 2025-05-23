package com.ciot.deliverywear.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ThreadUtils
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.bean.EventBusBean
import com.ciot.deliverywear.bean.NavPointResponse
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.constant.NetConstant
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
import com.ciot.deliverywear.utils.MyLog
import com.ciot.deliverywear.utils.SPUtils
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() , View.OnClickListener {
    private var mCompositeDisposable: CompositeDisposable? = null
    private var spUtils: SPUtils? = null

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
        MyLog.d(TAG, "setBindInfo key: $key")
        spUtils?.getInstance()?.putString(ConstantLogic.BIND_KEY, key)
        spUtils?.getInstance()?.putString(ConstantLogic.BIND_SERVER, server)
        spUtils?.getInstance()?.putBoolean(ConstantLogic.IS_BOUND, true)
        spUtils?.getInstance()?.putBoolean(ConstantLogic.IS_FIRST_TIME_LAUNCH, false)
    }

    private lateinit var binding: ActivityMainBinding
    private var timeTextView: TextView? = null
    private var returnView: ImageView? = null
    private var cancelView: ImageView? = null
    private var myRobotText: TextView? = null
    private var currentfragment: BaseFragment? = null
    private var showingFragment: Fragment? = null

    private val mRequestCode = 123
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.VIBRATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        MyLog.d(TAG, "MainActivity onCreate start")
        ContextUtil.setContext(this)
        spUtils = SPUtils()
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
        getCurTime()
        if (!checkPermissions(permissions)) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                permissions,
                mRequestCode
            )
        }
        initWatch()
    }

    override fun onResume() {
        super.onResume()
        initListener()
        if (spUtils?.getInstance()?.getBoolean(ConstantLogic.IS_FIRST_TIME_LAUNCH, true) == true) {
            showWelcome()
        }
        if (!EventBus.getDefault().isRegistered(this@MainActivity)) {
            EventBus.getDefault().register(this@MainActivity)
        }
        refreshHome()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this@MainActivity)
        EventBus.clearCaches()
        FragmentFactory.clearCache()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            // 检查用户是否授予了所请求的权限
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //MyLog.d(TAG, "MainActivity PermissionsResult true>>>>>>>>>")
            } else {
                //MyLog.d(TAG, "MainActivity PermissionsResult false>>>>>>>>>")
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                MyLog.d(TAG, "MainActivity PermissionsResult true>>>>>>>>>")
            } else {
                MyLog.d(TAG, "MainActivity PermissionsResult false>>>>>>>>>")
            }
        }

    private fun checkPermissions(neededPermissions: Array<String>): Boolean {
        if (neededPermissions.isEmpty()) {
            return true
        }
        var allGranted = true
        for (neededPermission in neededPermissions) {
            val isGranted = ContextCompat.checkSelfPermission(ContextUtil.getContext(), neededPermission) == PackageManager.PERMISSION_GRANTED
//            if (!isGranted) {
//                requestPermissionLauncher.launch(
//                    neededPermission)
//            }
            allGranted = allGranted and isGranted
        }
        MyLog.d(TAG, "MainActivity checkPermissions allGranted: $allGranted")
        return allGranted
    }

    private fun initData() {
        if (spUtils?.getInstance()?.getString(ConstantLogic.BIND_SERVER).isNullOrEmpty()) {
            spUtils?.getInstance()?.putString(ConstantLogic.BIND_SERVER, NetConstant.DEFAULT_SERVICE_URL)
        }
        spUtils?.getInstance()?.getString(ConstantLogic.BIND_SERVER)
            ?.let { RetrofitManager.instance.setDefaultServer(it) }
        RetrofitManager.instance.setTcpIp(NetConstant.IP_DEV)
    }

    private fun showStandby() {
        if (currentfragment is StandbyFragment) {
            return
        }
        MyLog.d(TAG, "MainActivity showStandby >>>>>>>>>")
        val dealResult = DealResult()
        dealResult.type = ConstantLogic.MSG_TYPE_STANDBY
        updateFragment(ConstantLogic.MSG_TYPE_STANDBY, dealResult)
    }

    fun showHome() {
        if (currentfragment is HomeFragment) {
            return
        }
        showLoadingDialog()
        MyLog.d(TAG, "MainActivity showHome >>>>>>>>>")
        RetrofitManager.instance.getRobotsForHome(false)
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
                    MyLog.d(TAG, "NavPointResponse: " + GsonUtils.toJson(response))
                    RetrofitManager.instance.parsePointAllResponseBody(response)
                    val dealResult = DealResult()
                    dealResult.pointInfoList = RetrofitManager.instance.getPoints()
                    dealResult.selectRobotId = robotId
                    updateFragment(ConstantLogic.MSG_TYPE_POINT, dealResult)
                }

                override fun onError(e: Throwable) {
                    MyLog.e(TAG, "点位获取失败 ：${e.message}")
                }

                override fun onComplete() {

                }
            })
    }

    private fun showWelcome() {
        MyLog.d(TAG, "MainActivity showWelcome >>>>>>>>>")
        updateFragment(ConstantLogic.MSG_TYPE_WELCOME, null)
    }

    private fun showSetting() {
        MyLog.d(TAG, "MainActivity showSetting >>>>>>>>>")
        updateFragment(ConstantLogic.MSG_TYPE_SETTING, null)
    }

    // 加载动画弹窗
    private var mLoadingDialog: LoadingDialog? = null
    private var dismissLoadingRunnable: Runnable? = null
    private var dismissLoadingDelay = 5000L // 5s后未完成加载就取消加载动画
    private fun showLoadingDialog() {
        if (mLoadingDialog?.dialog?.isShowing != true) {
            mLoadingDialog = LoadingDialog()
            mLoadingDialog?.show(supportFragmentManager, "ScanLoadingDialog")
            MyLog.d(TAG,"showLoadingDialog>>>>>>")

            dismissLoadingRunnable = Runnable {
                dismissLoadingDialog()
            }
            handler.postDelayed(dismissLoadingRunnable!!, dismissLoadingDelay)
        }
    }
    private fun dismissLoadingDialog() {
        if (mLoadingDialog?.dialog?.isShowing == true) {
            mLoadingDialog?.dismiss()
        }
        cancelDismissLoading()
    }

    private fun cancelDismissLoading() {
        dismissLoadingRunnable?.let {
            handler.removeCallbacks(it)
            dismissLoadingRunnable = null
        }
    }

    private fun initListener() {
        MyLog.d(TAG, "initListener start")
        returnView?.setOnClickListener(this)
        cancelView?.setOnClickListener(this)
    }

    private var loginRetryCount: Int = 1
    private fun initWatch() {
        MyLog.d(TAG, "initWatch start")
        var mac = MyDeviceUtils.getMacAddress()
        if (mac.isNullOrEmpty()) {
            mac = "02:00:00:00:00:00"
            MyLog.e(TAG, "initWatch can not get mac!")

        }
        MyLog.d(TAG, "initWatch mac=$mac")
        RetrofitManager.instance.setWuHanUserName(mac)
        val code = spUtils?.getInstance()?.getString(ConstantLogic.BIND_KEY)
        if (spUtils?.getInstance()?.getBoolean(ConstantLogic.IS_BOUND) == true && code != null) {
            MyLog.d(TAG, "project code isBound, code=$code")
            showLoadingDialog()
            RetrofitManager.instance.setWuHanPassWord(code)
            firstLogin()
        }
    }

    private fun firstLogin() {
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
                    if (loginRetryCount <= 5) {
                        ThreadUtils.getMainHandler().postDelayed({
                            firstLogin()
                        }, 2000)
                    } else {
                        MyLog.e(TAG, " initWatch err count: $loginRetryCount")
                    }
                    loginRetryCount++
                    MyLog.w(TAG,"initWatch onError: ${e.message}")
                }

                override fun onComplete() {
                    RetrofitManager.instance.init()
                    showHome()
                }
            })
    }

    private fun initView() {
        MyLog.d(TAG, "initView start")
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
                    if (spUtils?.getInstance()?.getBoolean(ConstantLogic.IS_FIRST_TIME_LAUNCH, true) == true) {
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

    private var refreshTimer: Timer? = null
    private fun refreshHome() {
        cancelRefreshTimer()
        refreshTimer = Timer()
        refreshTimer!!.schedule(0, 2000) {
            if (currentfragment is HomeFragment) {
                RetrofitManager.instance.getRobotsForHome(true)
            }
        }
    }
    private fun cancelRefreshTimer() {
        refreshTimer?.cancel()
        refreshTimer = null
    }

    private lateinit var curTimeHandler: Handler
    private lateinit var updateTimeRunnable: Runnable
    private fun getCurTime() {
        curTimeHandler = Handler()
        updateTimeRunnable = object : Runnable {
            override fun run() {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.headView.timeTextView.text = timeFormat.format(Date())
                curTimeHandler.postDelayed(this, 1000)
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
        cancelRefreshTimer()
        curTimeHandler.removeCallbacksAndMessages(null)
        dismissLoadingDialog()
        RetrofitManager.instance.getTcpClient()?.disconnect()
        RetrofitManager.instance.onUnsubscribe()
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
            MyLog.d(TAG, "changeFragment current fragment == null")
            return
        }

        if (newFragment == showingFragment) {
            MyLog.d(TAG, "changeFragment newFragment->${newFragment.javaClass.simpleName} == showingFragment>${showingFragment?.javaClass?.simpleName}")
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(bean: EventBusBean?) {
        MyLog.d(TAG, "MainActivity handleEvent message:" + bean!!.toString())
        when (bean.eventType) {
            ConstantLogic.EVENT_ARRIVED_POINT -> {
                val arrivedPoint = bean.content
                val dealResult = DealResult()
                dealResult.selectPoint = arrivedPoint
                dealResult.navInfo = "Arriving To "
                dealResult.isVibratorOn = true
                updateFragment(ConstantLogic.MSG_TYPE_HEADING, dealResult)
            }
            ConstantLogic.EVENT_RECONNECT_TCP -> {
                RetrofitManager.instance.initTcpService()
            }
            ConstantLogic.EVENT_SHOW_HOME -> {
                val dealResult = DealResult()
                dealResult.type = ConstantLogic.MSG_TYPE_HOME
                dealResult.robotInfoList = RetrofitManager.instance.getRobotData()
                MyLog.d(TAG, "showHome dealResult: " + GsonUtils.toJson(dealResult))
                dismissLoadingDialog()
                updateFragment(ConstantLogic.MSG_TYPE_HOME, dealResult)
                resetTimer()
            }
            ConstantLogic.EVENT_REFRESH_HOME -> {
                val dealResult = DealResult()
                dealResult.type = ConstantLogic.MSG_TYPE_HOME
                dealResult.robotInfoList = RetrofitManager.instance.getRobotData()
                if (currentfragment is HomeFragment) {
                    currentfragment?.refreshData(false, dealResult)
                }
            }
        }
    }
}
