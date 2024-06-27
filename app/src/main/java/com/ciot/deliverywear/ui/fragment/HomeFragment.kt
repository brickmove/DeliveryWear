package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.ui.adapter.RobotCardAdapter
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.custom.RobotCardDecoration
import java.util.LinkedList


class HomeFragment: BaseFragment() {
    private var recyclerView: RecyclerView? = null
    private var adapter: RobotCardAdapter? = null
    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_home , container , false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.robot_list_view)
        val robotIds = listOf("YHDE1230D005B0SZGM2822002008")
        val statuses = listOf("90%")
        adapter = RobotCardAdapter(requireContext(), robotIds, statuses)
        recyclerView?.adapter = adapter
        val spaceItemDecoration = RobotCardDecoration(12, 16)
        recyclerView?.addItemDecoration(spaceItemDecoration)
    }

    override fun refreshData(isRefreshImmediately: Boolean, dataList: LinkedList<out DealResult>?) {
        //先传递数据过来.再通过显示数据
        if (dataList == null || dataList.size <= 0) {
            Log.w(TAG, "HomeFragment dataList== null")
            return
        }

    }
}