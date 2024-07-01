package com.ciot.deliverywear.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.RobotData
import com.ciot.deliverywear.ui.widgets.CustomClickListener

class RobotCardAdapter(
    private val context: Context,
    private val robotData: List<RobotData>):
    RecyclerView.Adapter<RobotCardAdapter.RobotCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RobotCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_robot_card, parent, false)
        val holder = RobotCardViewHolder(view)
//        holder.itemView.setOnClickListener(object : CustomClickListener() {
//            override fun onSingleClick(v: View) {
//                mOnViewItemClickListener?.onRobotClick(v)
//            }
//        })
        return holder
    }

    override fun getItemCount(): Int {
        return robotData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RobotCardViewHolder, position: Int) {
        holder.robotId.text = robotData[position].id
        holder.battery.text = robotData[position].battery.toString() + "%"
        holder.summonButton.setOnClickListener(object : CustomClickListener() {
            override fun onSingleClick(v: View) {
                //mOnViewItemClickListener?.onRobotClick(v)
                summonButtonClickListener?.onSummonClick(holder.adapterPosition)
            }

        })
    }

    private var mOnViewItemClickListener: OnRobotClickListener? = null
    interface OnRobotClickListener {
        fun onRobotClick(view: View)
    }

    interface OnSummonClickListener {
        fun onSummonClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnRobotClickListener) {
        this.mOnViewItemClickListener = listener
    }
    fun setSummonButtonClickListener(listener: OnSummonClickListener) {
        this.summonButtonClickListener = listener
    }
    private var summonButtonClickListener: OnSummonClickListener? = null

    inner class RobotCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val robotId: TextView = itemView.findViewById(R.id.robot_id)
        internal val battery: TextView = itemView.findViewById(R.id.robot_battery)
        internal val summonButton: Button = itemView.findViewById(R.id.robot_summon)
    }
}