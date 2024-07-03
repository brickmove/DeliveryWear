package com.ciot.deliverywear.bean

/**
 * Created by p'c on 2024/6/7.
 * Description:
 * Encoding: utf-8
 */
class DealResult {
    var type: Int = 0

    // 界面数据设置
    var selectRobotId: String? = null
    var selectPoint: String? = null
    var navInfo: String? = null
    var robotList: List<String>? = null
    var robotInfoList: List<RobotData>? = null
    var areaInfoList: List<String>? = null
    var pointInfoList: List<String>? = null
    override fun toString(): String {
        return "DealResult(type=$type, " +
                "selectRobotId, $selectRobotId" +
                "selectPoint, $selectPoint" +
                "robotList = $robotList, " +
                "robotInfoList = $robotInfoList, " +
                "areaInfoList = $areaInfoList" +
                "pointInfoList = $pointInfoList" +
                ")"
    }
}