package com.ciot.deliverywear.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ciot.deliverywear.R
import com.ciot.deliverywear.bean.RobotData
import com.ciot.deliverywear.network.RetrofitManager


class RobotCardAdapter(
    private val context: Context,
    private val robotData: List<RobotData>):
    RecyclerView.Adapter<RobotCardAdapter.RobotCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RobotCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_robot_card, parent, false)
        return RobotCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return robotData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RobotCardViewHolder, position: Int) {
        holder.robotId.text = robotData[position].id
        holder.battery.text = robotData[position].battery.toString() + "%"
    }

    inner class RobotCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val robotId: TextView = itemView.findViewById(R.id.robot_id)
        val battery: TextView = itemView.findViewById(R.id.robot_battery)
        private val summonButton: Button = itemView.findViewById(R.id.robot_summon)
        init {
            summonButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (v?.id == R.id.robot_summon) {
                //change fragment
                Log.d("MainActivity", "click position=$position")
            }
        }
    }
}