package com.ciot.deliverywear.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ciot.deliverywear.R

class PointCardAdapter(private val context: Context, private val points: List<String>):
    RecyclerView.Adapter<PointCardAdapter.PointCardViewHolder>() {
    private var selectedPosition: Int? = -1

    @Volatile
    private var selectedName: String? =null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_point_card, parent, false)
        return PointCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return points.size
    }

    override fun onBindViewHolder(holder: PointCardViewHolder, position: Int) {
        holder.pointName.text = points[position]
        holder.itemView.setBackgroundResource(
            if (selectedPosition == position) {
                selectedName = points[position]
                R.drawable.point_background_select
            } else {
                R.drawable.point_background_normal
            }
        )
        holder.pointName.setTextColor(
            if (selectedPosition == position) {
                ContextCompat.getColor(context, R.color.yellow)
            } else {
                ContextCompat.getColor(context, R.color.white)
            }
        )
    }

    inner class PointCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pointName: TextView = itemView.findViewById(R.id.point_name)

        init {
            pointName.setOnClickListener{
                selectItem(adapterPosition)
            }
        }
    }

    private fun selectItem(position: Int) {
        if (selectedPosition != null) {
            notifyItemChanged(selectedPosition!!)
        }
        selectedPosition = position
        notifyItemChanged(position)
    }

    fun getPosition(): Int? {
        return selectedPosition
    }

    fun getPositionName(): String? {
        return selectedName
    }
}