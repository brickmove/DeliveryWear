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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.GsonUtils
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.bean.NavPointResponse
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.databinding.*
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.fragment.FragmentFactory
import com.ciot.deliverywear.ui.fragment.HomeFragment
import com.ciot.deliverywear.ui.fragment.PointFragment
import com.ciot.deliverywear.ui.fragment.StandbyFragment
import com.ciot.deliverywear.utils.ContextUtil
import com.ciot.deliverywear.utils.MyDeviceUtils
import com.ciot.deliverywear.utils.PrefManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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

    private lateinit var binding: ActivityMainBinding
    private var timeTextView: TextView? = null
    private var welcomeSmile: ImageView? = null
    private var welcomeWords: TextView? = null
    private var returnView: ImageView? = null
    private var cancelView: ImageView? = null
    private var myRobotText: TextView? = null
    private var enterPassword: CardView? = null
    private var deviceImgView: ImageView? = null
    private var currentfragment: BaseFragment? = null
    private var showingFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "MainActivity onCreate start")
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
        initWatch()
        initView()
        getCurTime()
    }

    override fun onResume() {
        super.onResume()
        initListener()
        Log.d(TAG, "prefManager.bindKey: " + prefManager?.bindKey)
        if (prefManager?.bindKey.isNullOrEmpty()) {
            initAnimation()
        } else {
            showWelcome()
        }
        resetTimer()
    }
    private fun showStandby() {
        if (currentfragment is StandbyFragment) {
            return
        }
        Log.d(TAG, "MainActivity showStandby >>>>>>>>>")
        val dealResult = DealResult()
        dealResult.type = ConstantLogic.MSG_TYPE_STANDBY
        updateFragment(ConstantLogic.MSG_TYPE_HEADING, dealResult)
        //updateFragment(ConstantLogic.MSG_TYPE_STANDBY, dealResult)
    }

    fun showHome() {
        if (currentfragment is HomeFragment) {
            return
        }
        Log.d(TAG, "MainActivity showHome >>>>>>>>>")
        val dealResult = DealResult()
        dealResult.type = ConstantLogic.MSG_TYPE_HOME
        dealResult.robotInfoList = RetrofitManager.instance.getRobotData()
        Log.d(TAG, "showHome dealResult: " + GsonUtils.toJson(dealResult))
        updateFragment(ConstantLogic.MSG_TYPE_HOME, dealResult)
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

    private fun initAnimation() {
        if (!RetrofitManager.instance.getIsLoading()!!) {
            showLoadingView()
            Handler().postDelayed({
                // 数据加载完成后隐藏加载布局，显示主要内容的部分
                hideLoadingView()
                RetrofitManager.instance.setIsLoading(true)
                showHome()
            }, 1000) // 数据加载延迟
        } else {
            showHome()
        }
    }

    private fun showLoadingView() {
        Log.d(TAG, "MainActivity showLoadingView")
        binding.loadingView.loadingLayout.visibility = View.VISIBLE
        binding.headView.timeTextView.visibility = View.GONE
        binding.headView.myRobot.visibility = View.GONE
    }

    private fun hideLoadingView() {
        Log.d(TAG, "MainActivity hideLoadingView")
        binding.loadingView.loadingLayout.visibility = View.GONE
        binding.headView.timeTextView.visibility = View.VISIBLE
        binding.headView.myRobot.visibility = View.VISIBLE
    }

    private fun initListener() {
        Log.d(TAG, "initListener start")
        enterPassword?.setOnClickListener(this)
        deviceImgView?.setOnClickListener(this)
        returnView?.setOnClickListener(this)
//        findViewById<View>(android.R.id.content).setOnTouchListener { _, _ ->
//            resetTimer()
//            true
//        }
    }

    private fun initWatch() {
        Log.d(TAG, "initWatch start")
        val mac = MyDeviceUtils.getMacAddress()
        RetrofitManager.instance.setWuHanUserName(mac)
        // 接口测试
        RetrofitManager.instance.setWuHanPassWord("17923345")
        //RetrofitManager.instance.setWuHanPassWord("40399636")
        RetrofitManager.instance.toLogin()
//        val code = prefManager?.bindKey
//        if (prefManager?.isBound == true && code != null) {
//            RetrofitManager.instance.setWuHanPassWord(code)
//            RetrofitManager.instance.toLogin()
//        }
    }

    private fun initView() {
        Log.d(TAG, "initView start")
        timeTextView = findViewById(R.id.timeTextView)
        welcomeSmile = findViewById(R.id.welcome_smile)
        welcomeWords = findViewById(R.id.welcome_words)
        enterPassword = findViewById(R.id.enter_password)
        deviceImgView = findViewById(R.id.device_image)
        returnView = findViewById(R.id.return_img)
        cancelView = findViewById(R.id.cancel_img)
        myRobotText = findViewById(R.id.my_robot)

//        returnView?.visibility = View.INVISIBLE
//        myRobotText?.visibility = View.INVISIBLE

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
        Log.d(TAG, "---onClick id---" + view?.id)
        when (view?.id) {
            R.id.enter_password -> {
                Log.d(TAG, "---enter_password---")
                //updateFragment(ConstantLogic.MSG_TYPE_LOGIN)
                RetrofitManager.instance.getRobots()
            }
            R.id.button_got_it -> {

            }
            R.id.device_image -> {
                updateFragment(ConstantLogic.MSG_TYPE_POINT, null)
            }
            R.id.return_img -> {
                if (currentfragment is PointFragment) {
                    showHome()
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 捕捉所有触摸事件
        resetTimer()
        return super.dispatchTouchEvent(ev)
    }
    private fun resetTimer() {
        // 重置计时器
        handler.removeCallbacks(standbyRunnable)
        handler.postDelayed(standbyRunnable, delayMillis)
    }
    private val handler = Handler(Looper.getMainLooper())
    private val standbyRunnable = Runnable {
        // 进入待机页面
        showStandby()
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
        onUnsubscribe()
        handler.removeCallbacks(standbyRunnable)
        curTimeHandler.removeCallbacksAndMessages(null)
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
            || type == ConstantLogic.MSG_TYPE_LOGIN
            || type == ConstantLogic.MSG_TYPE_SETTING
            || type == ConstantLogic.MSG_TYPE_HEADING
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
            ConstantLogic.MSG_TYPE_POINT, ConstantLogic.MSG_TYPE_LOGIN -> {
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
