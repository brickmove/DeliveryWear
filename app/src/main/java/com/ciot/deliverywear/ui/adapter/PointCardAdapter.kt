package com.ciot.deliverywear.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ciot.deliverywear.R


class PointCardAdapter(private val context: Context, private val points: List<String>):
    RecyclerView.Adapter<PointCardAdapter.PointCardViewHolder>() {
    private var pointPosition: Int? = -1
    private var isClicked :Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_point_card, parent, false)
        return PointCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return points.size
    }

    override fun onBindViewHolder(holder: PointCardViewHolder, position: Int) {
        holder.pointName.text = points[position]
    }

    inner class PointCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener{
        val pointName: TextView = itemView.findViewById(R.id.point_name)

        init {
            pointName.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            val localPosition = adapterPosition
            if (v?.id == R.id.point_name) {
                Log.d("MainActivity", "click position=$position")
                // 重复点击同一个点位
                if (localPosition == pointPosition) {
                    if (isClicked) {
                        v.setBackgroundResource(R.drawable.point_background_select)
                        isClicked = false
                    } else {
                        v.setBackgroundResource(R.drawable.point_background_normal)
                        isClicked = true
                    }
                } else {
                    v.setBackgroundResource(R.drawable.point_background_select)
                    isClicked = false
                }
            }
        }
    }
}