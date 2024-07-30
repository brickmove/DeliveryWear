package com.ciot.deliverywear.bean

/**
 * Created by p'c on 2024/7/9.
 * Description:
 * Encoding: utf-8
 */
class AllStatusResponse {
    private var navigation: Navigation? = null
    inner class Navigation {
        private var state: Int? = null
        private var taskstate: Int? = null
        fun getState() :Int? {
            return state
        }

        fun getTaskState() :Int? {
            return taskstate
        }
    }

    fun getNavigation(): Navigation? {
        return navigation
    }

    private var taskstate: String? = null
    private var battery: Battery? = null

    inner class Battery {
        private var capacity: Int? = null

        fun getCapacity() : Int? {
            return capacity
        }
    }

    fun getBattery(): Battery? {
        return battery
    }
}