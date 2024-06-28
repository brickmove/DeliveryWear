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
import com.ciot.deliverywear.bean.RobotData
import com.ciot.deliverywear.ui.adapter.RobotCardAdapter
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.custom.RobotCardDecoration

class HomeFragment: BaseFragment() {
    private var recyclerView: RecyclerView? = null
    private var adapter: RobotCardAdapter? = null
    private val mDataList: MutableList<RobotData> = ArrayList()
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
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d(TAG, "HomeFragment onScrolled: dx = $dx, dy = $dy")
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
}