package com.ciot.deliverywear.constant


object HttpConstant {
    const val TAG = "NETWORK_TAG"
    const val HTTP_URL = "http://"
    const val DEFAULT_TIMEOUT: Long = 60
    // 平台地址
    const val IP: String = "dev.csstrobot.com"
    val DEFAULT_SERVICE_URL = "http://$IP:9899/"

    // 是否绑定平台
    var isBind: Boolean = false

    // 密钥文件
    var bindFile: String = ""

    /*武汉服务器初始化状态*/
    //0表示获取到TCP长连接的IP;1表示激活成功获取到账户和密码;2表示登录成功;3表示获取到token;4表示获取到projectId等属性信息
    const val INIT_STATE_IDLE = -1
    const val INIT_STATE_GET_IP = 0
    const val INIT_STATE_GET_USER = 1
    const val INIT_STATE_LONGIN_GET_TOKEN = 2
    const val INIT_STATE_GET_PROPERTITY = 3
    const val INIT_STATE_INIT_EXCEPTION = 4
}