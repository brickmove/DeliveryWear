package com.ciot.deliverywear.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomeFragment: BaseFragment() {
    private var recyclerView: RecyclerView? = null
    private var adapter: RobotCardAdapter? = null
    private val mDataList: MutableList<RobotData> = ArrayList()
    private var mRobotDisposable: CompositeDisposable? = null
    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_home , container , false)
        recyclerView = view.findViewById(R.id.robot_list_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAdapter()
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
        //adapter?.setOnItemClickListener(object : RobotCardAdapter.OnRobotClickListener  {
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
        if (mDataList.size > 0) {
            mDataList.clear()
        }

        data.robotInfoList?.map {
            val robotData = RobotData()
            robotData.id = it.id
            robotData.battery= 60
            mDataList.add(robotData)
        }
        Log.w(TAG, "HomeFragment mDataList: " + GsonUtils.toJson(mDataList))
        adapter?.notifyDataSetChanged()
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