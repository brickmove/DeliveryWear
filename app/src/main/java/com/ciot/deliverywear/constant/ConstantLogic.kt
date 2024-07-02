package com.ciot.deliverywear.constant

interface ConstantLogic {
    companion object {
        // 日志TAG标签
        const val TIME_TEST = "TIME_TEST"
        const val FRAGMENT = "FRAGMENT"

        // fragment
        const val MSG_TYPE_HOME = 0 // 主页
        const val MSG_TYPE_WELCOME = 1 // 首次登陆页面
        const val MSG_TYPE_LOGIN = 2 // 绑定密钥页面
        const val MSG_TYPE_AREA = 3 // 区域页面
        const val MSG_TYPE_POINT = 4 // 点位页面
        const val MSG_TYPE_SETTING = 5 // 设置页面
        const val MSG_TYPE_HEADING = 6 // 前往目标点页面
        const val MSG_TYPE_STANDBY = 99 // 待机页面

    }
}