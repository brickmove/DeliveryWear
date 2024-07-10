package com.ciot.deliverywear.network.tcp

import com.ciot.deliverywear.bean.ArrivedBean
import com.ciot.deliverywear.bean.EventBusBean
import com.ciot.deliverywear.bean.ResultBean
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.constant.NetConstant
import com.ciot.deliverywear.network.RetrofitManager
import org.greenrobot.eventbus.EventBus

/**
 * Created by p'c on 2024/7/8.
 * Description: 接收服务端消息
 * Encoding: utf-8
 */
class TcpMsgListener: TcpClientListener {
    private var TAG = ConstantLogic.NETWORK_TAG
    override fun onMessageReceived(message: ByteArray) {
        // 解析并处理收到的消息
        val receive = TcpRequestUtils.bytes2Bean(message)
        //Log.d(TAG, "TcpMsgListener receive: " + GsonUtils.toJson(receive))
        if (receive.cmd == NetConstant.CONTROL_DEVICE_MANAGEMENT_REGISTER) {
            val response: ResultBean = receive.body as ResultBean
            if (response.result == 0) {
                RetrofitManager.instance.getTcpClient()?.startHeartbeat() // 注册成功后开始发送心跳指令
            } else {
                RetrofitManager.instance.getTcpClient()?.startReconnect()
            }
        } else if (receive.cmd == NetConstant.CONTROL_STATUS_ARRIVED_POINT) {
            val response: ArrivedBean = receive.body as ArrivedBean
            val eventBusBean = EventBusBean()
            eventBusBean.eventType = ConstantLogic.EVENT_ARRIVED_POINT
            eventBusBean.content = response.info
            EventBus.getDefault().post(eventBusBean)
        }
    }
}