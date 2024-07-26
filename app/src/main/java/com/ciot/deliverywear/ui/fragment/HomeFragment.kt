package com.ciot.deliverywear.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.GsonUtils
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.bean.NavPointResponse
import com.ciot.deliverywear.bean.RobotData
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.adapter.RobotCardAdapter
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.custom.RobotCardDecoration
import com.ciot.deliverywear.utils.FormatUtil
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomeFragment: BaseFragment() {
    private var recyclerView: RecyclerView? = null
    private var settingButton: CardView? = null
    private var noRobotView: ImageView? = null
    private var noRobotText: TextView? = null
    private var adapter: RobotCardAdapter? = null
    private val mDataList: MutableList<RobotData> = ArrayList()
    private var mRobotDisposable: CompositeDisposable? = null
    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_home , container , false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        recyclerView = view?.findViewById(R.id.robot_list_view)
        settingButton = view?.findViewById(R.id.settingButton)
        noRobotView = view?.findViewById(R.id.no_robot_img)
        noRobotText = view?.findViewById(R.id.no_robot_tx)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initListener()
    }

    private fun initListener() {
        settingButton?.setOnClickListener{
            val dealResult = DealResult()
            (activity as MainActivity).updateFragment(ConstantLogic.MSG_TYPE_SETTING, dealResult)
        }
    }

    private fun initAdapter() {
        mDataList.clear()
        adapter = RobotCardAdapter(activity?.applicationContext!!, mDataList)
        recyclerView?.adapter = adapter
        val spaceItemDecoration = RobotCardDecoration(12, 16)
        recyclerView?.addItemDecoration(spaceItemDecoration)
        //监听滚动
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //Log.d(TAG, "HomeFragment onScrolled: dx = $dx, dy = $dy")
            }
        })
        adapter?.setSummonButtonClickListener(object : RobotCardAdapter.OnSummonClickListener  {
            override fun onSummonClick(position: Int) {
                if (position <= RecyclerView.NO_POSITION) {
                    //防止异常
                    return
                }
                val robotId = RetrofitManager.instance.getRobotList()?.get(position)
                Log.d(TAG, "robotId: $robotId")
                if (!robotId.isNullOrEmpty()) {
                    onUnsubscribe()
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
                                (activity as MainActivity).updateFragment(ConstantLogic.MSG_TYPE_POINT, dealResult)
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "点位获取失败 ：${e.message}")
                            }

                            override fun onComplete() {

                            }
                        })
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refreshData(isRefreshImmediately: Boolean, data: DealResult) {
        Log.w(TAG, "HomeFragment refreshData: " + GsonUtils.toJson(data))
//        if (isDetached){
//            return
//        }
        if (mDataList.size > 0) {
            mDataList.clear()
        }
        settingButton?.visibility = View.VISIBLE
        if (data.robotInfoList?.size == 0) {
            recyclerView?.visibility = View.GONE
            noRobotView?.visibility = View.VISIBLE
            noRobotText?.visibility = View.VISIBLE
        } else {
            recyclerView?.visibility = View.VISIBLE
            noRobotView?.visibility = View.GONE
            noRobotText?.visibility = View.GONE
            data.robotInfoList?.map {
                val robotData = RobotData()
                robotData.id = it.id
                robotData.name = it.name
                robotData.link = it.link
                robotData.label = it.label?.let { it1 -> FormatUtil.formatLable(it1) }
                if (it.battery == null) {
                    robotData.battery = 60
                } else {
                    robotData.battery = it.battery
                }
                mDataList.add(robotData)
            }
            Log.w(TAG, "HomeFragment mDataList: " + GsonUtils.toJson(mDataList))
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            onUnsubscribe()
        }
    }

    fun onUnsubscribe() {
        mRobotDisposable?.clear()
    }

    fun addSubscription(disposable: Disposable?) {
        if (mRobotDisposable == null) {
            mRobotDisposable = CompositeDisposable()
        }
        disposable?.let { mRobotDisposable?.add(it) }
    }
}