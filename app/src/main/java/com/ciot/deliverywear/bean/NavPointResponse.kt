package com.ciot.deliverywear.bean

class NavPointResponse {
    /**
     * 导航点名称
     */
    private var positionName: String? = null

    /**
     * X轴坐标
     */
    private var x = 0

    /**
     * Y轴坐标
     */
    private var y = 0

    private var z = 0

    /**
     * 角度
     */
    private var angle = 0f

    /**
     * 楼层
     */
    private var mapInfo: String? = null

    /**
     * 地图名称
     */
    private var mapName: String? = null

    fun getPositionName(): String? {
        return positionName
    }

    fun setPositionName(positionName: String?) {
        this.positionName = positionName
    }

    fun getX(): Int {
        return x
    }

    fun setX(x: Int) {
        this.x = x
    }

    fun getY(): Int {
        return y
    }

    fun setY(y: Int) {
        this.y = y
    }

    fun getAngle(): Float {
        return angle
    }

    fun setAngle(angle: Float) {
        this.angle = angle
    }

    fun getMapInfo(): String? {
        return mapInfo
    }

    fun setMapInfo(floor: String?) {
        this.mapInfo = floor
    }

    fun getMapName(): String? {
        return mapName
    }

    fun setMapName(mapName: String?) {
        this.mapName = mapName
    }

    fun getZ(): Int {
        return z
    }

    fun setZ(z: Int) {
        this.z = z
    }

    override fun toString(): String {
        return "MarkerPoint{" +
                "positionName='" + positionName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", angle=" + angle +
                ", mapInfo=" + mapInfo +
                ", mapName='" + mapName + '\'' +
                '}'
    }
}
