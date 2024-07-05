package com.ciot.deliverywear.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.GsonUtils
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.network.RetrofitManager
import com.ciot.deliverywear.ui.MainActivity
import com.ciot.deliverywear.ui.adapter.PointCardAdapter
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.custom.PointCardDecoration
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject

class PointFragment : BaseFragment() {
    companion object {
        private const val TAG = "PointFragment"
    }
    private var recyclerView: RecyclerView? = null
    private var adapter: PointCardAdapter? = null
    private var summonButton: Button? = null
    private var returnView: ImageView? = null
    private var myRobotText: TextView? = null
    private var selectRobotText: TextView? = null
    private var selectRobot: String? = null
    private val mDataList: MutableList<String> = ArrayList()
    private var mPointDisposable: CompositeDisposable? = null

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_point , container , false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        returnView = view?.findViewById(R.id.return_img)
        myRobotText = view?.findViewById(R.id.my_robot)
        selectRobotText = view?.findViewById(R.id.point_robot_view)
        returnView?.visibility = View.INVISIBLE
        myRobotText?.visibility = View.VISIBLE
        summonButton = view?.findViewById(R.id.point_summon)
        recyclerView = view?.findViewById(R.id.point_list_view)
    }

    private fun initListener() {
        summonButton?.setOnClickListener {
            val pointName = getSelectedPositionName()
            Log.d(TAG, "click summon button, position: $pointName")
            if (selectRobot?.isNotEmpty() == true && pointName?.isNotEmpty() == true) {
                pointName.let { it1 ->
                    Log.d(TAG, "selectRobot: $selectRobot, pointName: $pointName")
                    RetrofitManager.instance.navPoint(selectRobot!!, it1)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe(object: Observer<ResponseBody> {
                            override fun onSubscribe(d: Disposable) {
                                addSubscription(d)
                            }

                            override fun onNext(body: ResponseBody) {
//                                val json = String(body.bytes())
//                                val res = JSONObject(json).getJSONObject("result")
//                                Log.d(TAG, "navigatePoint result:$res")
                                val dealResult = DealResult()
                                dealResult.selectPoint = it1
                                dealResult.navInfo = "Heading To "
                                (activity as MainActivity).updateFragment(ConstantLogic.MSG_TYPE_HEADING, dealResult)
                            }

                            override fun onError(e: Throwable) {
                                Log.w(TAG,"onError: ${e.message}")
                            }

                            override fun onComplete() {

                            }
                        })
                }
            }
        }
    }

    private fun getSelectedPositionName(): String? {
        return adapter?.getPositionName()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        mDataList.clear()
        adapter = PointCardAdapter(activity?.applicationContext!!, mDataList)
        recyclerView?.adapter = adapter
        val spaceItemDecoration = PointCardDecoration(12, 16)
        recyclerView?.addItemDecoration(spaceItemDecoration)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refreshData(isRefreshImmediately: Boolean, data: DealResult) {
        if (mDataList.size > 0) {
            mDataList.clear()
        }
        selectRobot = data.selectRobotId
        selectRobotText?.text = data.selectRobotId
        data.pointInfoList?.map {
            mDataList.add(it)
        }
        Log.w(TAG, "PointFragment mDataList: " + GsonUtils.toJson(mDataList))
        adapter?.notifyDataSetChanged()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            onUnsubscribe()
        }
    }

    private fun onUnsubscribe() {
        mPointDisposable?.clear()
    }

    fun addSubscription(disposable: Disposable?) {
        if (mPointDisposable == null) {
            mPointDisposable = CompositeDisposable()
        }
        disposable?.let { mPointDisposable?.add(it) }
    }
}