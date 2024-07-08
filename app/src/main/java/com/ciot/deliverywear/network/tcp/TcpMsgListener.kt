package com.ciot.deliverywear.network.tcp

import com.ciot.deliverywear.bean.ResultBean
import com.ciot.deliverywear.constant.NetConstant
import com.ciot.deliverywear.network.RetrofitManager

/**
 * Created by p'c on 2024/7/8.
 * Description: 接收服务端消息
 * Encoding: utf-8
 */
class TcpMsgListener: TcpClientListener {
    override fun onMessageReceived(message: String) {
        // 解析并处理收到的消息
        val receive = TcpRequestUtils.bytes2Bean(message.toByteArray())
        if (receive.cmd == NetConstant.CONTROL_DEVICE_MANAGEMENT_REGISTER) {
            val response: ResultBean = receive.body as ResultBean
            if (response.result == true) {
                RetrofitManager.instance.getTcpClient()?.startHeartbeat() // 注册成功后开始发送心跳指令
            } else {
                RetrofitManager.instance.getTcpClient()?.startReconnect()
            }
        }
    }
}