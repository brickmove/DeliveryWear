package com.ciot.deliverywear.ui.widgets

import androidx.recyclerview.widget.DiffUtil
import com.ciot.deliverywear.bean.RobotData

/**
 * Created by p'c on 2024/7/30.
 * Description:
 * Encoding: utf-8
 */
class RobotDiffCallback(private val oldList: List<RobotData>, private val newList: List<RobotData>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRobot = oldList[oldItemPosition]
        val newRobot = newList[newItemPosition]
        return oldRobot.name == newRobot.name && oldRobot.link == newRobot.link && oldRobot.label == newRobot.label && oldRobot.battery == newRobot.battery
    }
}
