package com.ciot.deliverywear.config

/**
 * Created by p'c on 2024/6/18.
 * Description:
 * Encoding: utf-8
 */
interface baseConfig {
    companion object {
        // 连续点击次数
        const val counts: Int = 8
        // 连续点击有效时间
        const val duration: Long = 2 * 1000
        // 无操作进入屏保时间
        const val sleepTime: Long = 15
    }
}