package com.ciot.deliverywear.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.adapter.PointCardAdapter
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.custom.PointCardDecoration
import com.ciot.deliverywear.ui.fragment.FragmentFactory
import com.ciot.deliverywear.utils.ContextUtil
import com.ciot.deliverywear.utils.MyDeviceUtils
import com.ciot.deliverywear.utils.PrefManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.LinkedList

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

    private var timeTextView: TextView? = null
    private var timeStandby: TextView? = null
    private var dateStandby: TextView? = null
    private var welcomeSmile: ImageView? = null
    private var welcomeWords: TextView? = null
    private var returnView: ImageView? = null
    private var myRobotText: TextView? = null
    private var enterPassword: CardView? = null
    private var deviceImgView: ImageView? = null
    private var currentfragment: BaseFragment? = null
    private var showingFragment: Fragment? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: PointCardAdapter? = null
    private var loadingLayout: View? = null
    private var mainContent: View? = null

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
        setContentView(R.layout.fragment_home)
        initWatch()
        initView()
        getCurTime()
    }

    override fun onResume() {
        super.onResume()
        initAnimation()
        initListener()
        showHome()
    }

    private fun showHome() {
        val dealResult = DealResult()
        if (prefManager?.bindKey?.isNotEmpty() == true) {
            updateFragment(ConstantLogic.MSG_TYPE_HOME, null)
        } else {
            updateFragment(ConstantLogic.MSG_TYPE_WELCOME, null)
        }
    }

    private fun initAnimation() {
        if (!RetrofitManager.instance.getIsLoading()!!) {
            showLoadingLayout()
            Handler().postDelayed({
                // 数据加载完成后隐藏加载布局，显示主要内容的部分
                hideLoadingLayout()
                RetrofitManager.instance.setIsLoading(true)
            }, 2000) // 模拟2秒的数据加载延迟
        }
    }

    private fun showLoadingLayout() {
        Log.d(TAG, "MainActivity showLoadingLayout")
        loadingLayout?.visibility = View.VISIBLE
    }

    private fun hideLoadingLayout() {
        Log.d(TAG, "MainActivity hideLoadingLayout")
        loadingLayout?.visibility = View.GONE
        setContentView(R.layout.fragment_point)
        recyclerView = findViewById(R.id.point_list_view)
        adapter = RetrofitManager.instance.getPoints()?.let { PointCardAdapter(this, it) }
        recyclerView?.adapter = adapter
        val spaceItemDecoration = PointCardDecoration(8, 16)
        recyclerView?.addItemDecoration(spaceItemDecoration)
    }

    private fun initListener() {
        enterPassword?.setOnClickListener(this)
        deviceImgView?.setOnClickListener(this)
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
        timeStandby = findViewById(R.id.standby_time)
        dateStandby = findViewById(R.id.standby_date)
        welcomeSmile = findViewById(R.id.welcome_smile)
        welcomeWords = findViewById(R.id.welcome_words)
        enterPassword = findViewById(R.id.enter_password)
        deviceImgView = findViewById(R.id.device_image)
        returnView = findViewById(R.id.return_img)
        myRobotText = findViewById(R.id.my_robot)
        returnView?.visibility = View.INVISIBLE
        myRobotText?.visibility = View.INVISIBLE

        loadingLayout = findViewById(R.id.loading_layout)
        mainContent = findViewById(R.id.point)
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
                //updateFragment(ConstantLogic.MSG_TYPE_LOGIN)
                RetrofitManager.instance.getRobots()
            }
            R.id.button_got_it -> {

            }
            R.id.device_image -> {
                updateFragment(ConstantLogic.MSG_TYPE_POINT, null)
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
        onUnsubscribe()
    }

    private fun updateFragment(type: Int, results: LinkedList<out DealResult>?) {
        getFragment(type)
        changeFragment(type, currentfragment)
        currentfragment?.refreshData(false, results)
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
            || type == ConstantLogic.MSG_TYPE_WELCOME
            ) {
            container_full.visibility = View.VISIBLE
            containerView = container_full
            container_main.visibility = View.GONE
        } else {
            container_main.visibility = View.VISIBLE
            containerView = container_main
            container_full.visibility = View.GONE
        }
        FragmentFactory.changeFragment(supportFragmentManager, containerView, newFragment)
        showingFragment = newFragment
    }
}
