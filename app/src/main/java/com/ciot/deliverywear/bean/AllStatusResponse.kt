package com.ciot.deliverywear.bean


/**
 * Created by p'c on 2024/7/9.
 * Description:
 * Encoding: utf-8
 */
class AllStatusResponse {
    private var state: String? = null
    private var taskstate: String? = null
    private var batteryInfo: BatteryInfo? = null

    inner class BatteryInfo {
        private var battery: Int? = null

        fun getBattery() : Int? {
            return battery
        }
    }

    fun getState(): String? {
        return state
    }

    fun getTaskState(): String? {
        return taskstate
    }

    fun getBatteryInfo(): BatteryInfo? {
        return batteryInfo
    }
}