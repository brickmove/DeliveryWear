package com.ciot.deliverywear.constant


object NetConstant {
    const val TAG = "NETWORK_TAG"
    const val HTTP_URL = "http://"
    const val DEFAULT_TIMEOUT: Long = 60
    // 是否绑定平台
    var isBind: Boolean = false
    // 密钥文件
    var bindFile: String = ""
    // 平台地址
    const val IP_DEV: String = "dev.csstrobot.com"
    private const val IP_CN: String = "robot.csstrobot.com"
    private const val IP_US: String = "usom.gfai-robotics.com"
    private const val IP_HK: String = "gfai-robotics.com"

    var DEFAULT_SERVICE_URL = "http://$IP_DEV:9899/"

    var DEV_SERVICE_URL = "http://$IP_DEV:9899/"
    var CN_SERVICE_URL = "https://$IP_CN:9899/"
    var US_SERVICE_URL = "https://$IP_US:9899/"
    var HK_SERVICE_URL = "https://$IP_HK:9899/"

    /*武汉服务器初始化状态*/
    //0表示获取到TCP长连接的IP;1表示激活成功获取到账户和密码;2表示登录成功;3表示获取到token;4表示获取到projectId等属性信息
    const val INIT_STATE_IDLE = -1
    const val INIT_STATE_GET_IP = 0
    const val INIT_STATE_GET_USER = 1
    const val INIT_STATE_LONGIN_GET_TOKEN = 2
    const val INIT_STATE_GET_PROPERTITY = 3
    const val INIT_STATE_INIT_EXCEPTION = 4

    /**
     * TCP服务端端口号
     */
    const val TCP_SERVER_PORT = 28969

    /**
     * 心跳包上报
     */
    const val CONTROL_STATUS_HEART_BEAT = 0xF002.toShort()

    /**
     * 机器人注册
     */
    const val CONTROL_DEVICE_MANAGEMENT_REGISTER = 0xF001.toShort()

    /**
     * 导航点到达通知
     */
    const val CONTROL_STATUS_ARRIVED_POINT = 0xE001.toShort()
}