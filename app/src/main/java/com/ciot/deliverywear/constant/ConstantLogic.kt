package com.ciot.deliverywear.constant

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface ConstantLogic {
    companion object {
        // 日志TAG标签
        const val TIME_TEST = "TIME_TEST"
        const val FRAGMENT = "FRAGMENT"
        const val NETWORK_TAG = "NETWORK_TAG"
        const val TCP_TAG = "TCP_TAG"
        const val HTTP_TAG = "HTTP_TAG"

        // fragment
        const val MSG_TYPE_HOME = 0 // 主页
        const val MSG_TYPE_WELCOME = 1 // 首次登陆页面
        const val MSG_TYPE_BIND = 2 // 绑定密钥页面
        const val MSG_TYPE_AREA = 3 // 区域页面
        const val MSG_TYPE_POINT = 4 // 点位页面
        const val MSG_TYPE_SETTING = 5 // 设置页面
        const val MSG_TYPE_HEADING = 6 // 前往目标点页面
        const val MSG_TYPE_GATEWAY = 7 // 设置页面-设置gateway
        const val MSG_TYPE_STANDBY = 99 // 待机页面

        // Event bus constant
        const val EVENT_ARRIVED_POINT = "EVENT_ARRIVED_POINT" // 到达点位
        const val EVENT_SHOW_HOME = "EVENT_SHOW_HOME"         // 显示首页
        const val EVENT_REFRESH_HOME = "EVENT_REFRESH_HOME"   // 刷新首页
        const val EVENT_RECONNECT_TCP = "EVENT_RECONNECT_TCP" // 重连tcp

        // SharedPreferences val
        const val BIND_KEY = "BIND_KEY"
        const val BIND_SERVER = "BIND_SERVER"
        const val IS_BOUND = "IS_BOUND"
        const val IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH"

        // file constant
        val APP_PATH: String = Environment.getExternalStorageDirectory().toString() + File.separator + "RobotLog"
        val LOG_TODAY_FILE_NAME: String = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())
        val Dir_LOG_TODAY: String = APP_PATH + File.separator + LOG_TODAY_FILE_NAME
    }
}