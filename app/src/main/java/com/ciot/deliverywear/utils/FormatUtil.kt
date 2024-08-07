package com.ciot.deliverywear.utils
import java.util.UUID

class FormatUtil {
    companion object {

        /*
         *  导航状态:-1: 空闲 0：导航中 1：等待导航 2：导航起点位置错误 3：导航刷新数据，需等待 4：机器人附近有障碍物 5：网络堵塞 6：导航拒绝 7：导航失败 8：导航成功 9：导航取消 99：其它
         */
        fun formatLable(lable: Int) :String {
            var newLable = ""
            newLable = when (lable) {
                // 空闲
                -1,8 -> "Idle Status"
                // 任务中
                0,1,3,4 -> "Task in Progress"
                // 任务异常
                2,5,6,7 -> "Task Abnormal"
                // 任务暂停
                9 -> "Task Pausing"
                else -> "Others"
            }
            return newLable
        }

        fun createNid(): String {
            val uuid = UUID.randomUUID()
            val uniqueId = uuid.toString()
            return uniqueId
        }
    }
}