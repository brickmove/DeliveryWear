package com.ciot.deliverywear.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        return holder
    }

    override fun getItemCount(): Int {
        return robotData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RobotCardViewHolder, position: Int) {
        if (robotData[position].id.isNullOrEmpty()) {
            holder.robotId.text = robotData[position].name
        } else {
            holder.robotId.text = robotData[position].id
        }
        if (robotData[position].link) {
            holder.summonButton.visibility = View.VISIBLE
            holder.robotStatus2.visibility = View.GONE
            if (robotData[position].battery!! > 20) {
                holder.lightningGreen.visibility = View.VISIBLE
                holder.lightningRed.visibility = View.GONE
                holder.battery.setTextColor(ContextCompat.getColor(context, R.color.state_green))
            } else {
                holder.lightningGreen.visibility = View.GONE
                holder.lightningRed.visibility = View.VISIBLE
                holder.battery.setTextColor(ContextCompat.getColor(context, R.color.state_red))
            }
            holder.battery.visibility = View.VISIBLE
            holder.battery.text = robotData[position].battery.toString() + "%"
            holder.summonButton.setOnClickListener(object : CustomClickListener() {
                override fun onSingleClick(v: View) {
                    summonButtonClickListener?.onSummonClick(holder.adapterPosition)
                }
            })

            holder.robotStatus.text = robotData[position].label
            if (robotData[position].label.isNullOrEmpty()) {
                holder.robotStatus.visibility = View.GONE
            } else {
                when (robotData[position].label) {
                    "Idle Status" -> {
                        holder.robotStatus.setTextColor(ContextCompat.getColor(context, R.color.state_green))
                    }
                    "Emergency stop", "Shutdown" -> {
                        holder.robotStatus.setTextColor(ContextCompat.getColor(context, R.color.state_red))
                        setCommonState(holder)
                    }
                    else -> {
                        holder.robotStatus.setTextColor(ContextCompat.getColor(context, R.color.state_orange))
                    }
                }
            }
        } else {
            setCommonState(holder)
        }
    }

    private fun setCommonState(holder: RobotCardViewHolder) {
        holder.summonButton.visibility = View.GONE
        holder.lightningGreen.visibility = View.GONE
        holder.lightningRed.visibility = View.VISIBLE
        holder.emergencyStop.visibility = View.GONE
        holder.battery.visibility = View.GONE
        holder.robotStatus.visibility = View.GONE
        holder.robotStatus2.visibility = View.VISIBLE
        holder.robotStatus2.setTextColor(ContextCompat.getColor(context, R.color.state_red))
    }

    interface OnSummonClickListener {
        fun onSummonClick(position: Int)
    }

    fun setSummonButtonClickListener(listener: OnSummonClickListener) {
        this.summonButtonClickListener = listener
    }
    private var summonButtonClickListener: OnSummonClickListener? = null

    inner class RobotCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val robotId: TextView = itemView.findViewById(R.id.robot_id)
        internal val battery: TextView = itemView.findViewById(R.id.robot_battery)
        internal val summonButton: Button = itemView.findViewById(R.id.robot_summon)
        internal val lightningGreen: ImageView = itemView.findViewById(R.id.robot_lighting_green)
        internal val lightningRed: ImageView = itemView.findViewById(R.id.robot_lighting_red)
        internal val robotStatus: TextView = itemView.findViewById(R.id.robot_status)
        internal val robotStatus2: TextView = itemView.findViewById(R.id.robot_status2)
        internal val emergencyStop: ImageView = itemView.findViewById(R.id.emergency_stop)
    }
}