package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealReault
import com.ciot.deliverywear.ui.adapter.RobotCardAdapter
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.custom.RobotCardDecoration
import java.util.LinkedList


class HomeFragment: BaseFragment() {
    private var mData : LinkedList<out DealReault>? = null
    private var mDealResult : DealReault? = null
    private var mAdapter: RobotCardAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: RobotCardAdapter? = null
    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_home , container , false)
        if (mData != null) {
            mData = null
        }
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

}