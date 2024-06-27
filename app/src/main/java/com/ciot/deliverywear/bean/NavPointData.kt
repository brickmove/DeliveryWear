package com.ciot.deliverywear.bean

class NavPointData {
    /**
     * 导航点名称
     */
    private var positionname: String? = null

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
    private var mapinfo: String? = null

    /**
     * 点位类型
     */
    private var type = 0

    fun getType(): Int {
        return type
    }

    fun setType(type: Int) {
        this.type = type
    }

    fun getPositionName(): String? {
        return positionname
    }

    fun setPositionName(positionName: String?) {
        this.positionname = positionName
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
        return mapinfo
    }

    fun setMapInfo(floor: String?) {
        this.mapinfo = floor
    }


    fun getZ(): Int {
        return z
    }

    fun setZ(z: Int) {
        this.z = z
    }

    override fun toString(): String {
        return "MarkerPoint{" +
                "positionName='" + positionname + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", angle=" + angle +
                ", type=" + type +
                ", mapinfo=" + mapinfo + '\'' +
                '}'
    }
}
