package com.ciot.deliverywear.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.DealResult
import com.ciot.deliverywear.ui.adapter.PointCardAdapter
import com.ciot.deliverywear.ui.base.BaseFragment
import com.ciot.deliverywear.ui.custom.PointCardDecoration
import java.util.LinkedList

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

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_point , container , false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        returnView = view?.findViewById(R.id.return_img)
        myRobotText = view?.findViewById(R.id.my_robot)
        returnView?.visibility = View.INVISIBLE
        myRobotText?.visibility = View.VISIBLE
        summonButton = view?.findViewById(R.id.point_summon)
        summonButton?.setOnClickListener {
            Log.d("MainActivity", "click summon button")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.point_list_view)
        val points = listOf("A101", "A102", "A103", "A104", "A105")
        adapter = PointCardAdapter(requireContext(), points)
        recyclerView?.adapter = adapter
        val spaceItemDecoration = PointCardDecoration(8, 16)
        recyclerView?.addItemDecoration(spaceItemDecoration)
    }

    override fun refreshData(isRefreshImmediately: Boolean, data: DealResult) {
        //先传递数据过来.再通过显示数据
        if (data == null) {
            Log.w(TAG, "PointFragment dataList== null")
            return
        }

    }
}