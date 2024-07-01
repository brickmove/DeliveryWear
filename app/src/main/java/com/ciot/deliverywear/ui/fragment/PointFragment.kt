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
import com.ciot.deliverywear.ui.adapter.PointCardAdapter
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.custom.PointCardDecoration

// 登录页面
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
    private val mDataList: MutableList<String> = ArrayList()
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
        summonButton?.setOnClickListener {
            Log.d("MainActivity", "click summon button")
        }
        recyclerView = view?.findViewById(R.id.point_list_view)
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

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refreshData(isRefreshImmediately: Boolean, data: DealResult) {
        Log.w(TAG, "PointFragment refreshData: " + GsonUtils.toJson(data))
        if (mDataList.size > 0) {
            mDataList.clear()
        }
        selectRobotText?.text = data.selectRobotId
        data.pointInfoList?.map {
            mDataList.add(it)
        }
        Log.w(TAG, "PointFragment mDataList: " + GsonUtils.toJson(mDataList))
        adapter?.notifyDataSetChanged()
    }
}