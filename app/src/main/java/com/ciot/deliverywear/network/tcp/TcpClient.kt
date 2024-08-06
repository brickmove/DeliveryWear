package com.ciot.deliverywear.network.tcp

import com.ciot.deliverywear.bean.EventBusBean
import com.ciot.deliverywear.constant.ConstantLogic
import com.ciot.deliverywear.utils.MyLog
import org.greenrobot.eventbus.EventBus
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

/**
 * Created by p'c on 2024/7/8.
 * Description: tcp长连接客户端
 * Encoding: utf-8
 */
class TcpClient(private val serverIp: String, private val serverPort: Int) {
    private val TAG = ConstantLogic.TCP_TAG
    private var socket: Socket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null
    private val timer = Timer(true) // 单一的Timer实例
    private var heartbeatTimer: TimerTask? = null
    private var reconnectTimer: TimerTask? = null
    private var reconnectInterval = 5000L
    private var hearBeatInterval = 1000L
    private var isReconnecting = false
    private var listener: TcpClientListener? = null

    companion object {
        @Volatile
        private var instance: TcpClient? = null

        fun getInstance(serverIp: String, serverPort: Int): TcpClient {
            return instance ?: synchronized(this) {
                instance ?: TcpClient(serverIp, serverPort).also { instance = it }
            }
        }
    }

    fun setListener(listener: TcpClientListener) {
        this.listener = listener
    }

    fun connectAndRegister() {
        Thread {
            try {
                socket = Socket(serverIp, serverPort)
                output = socket?.getOutputStream()
                input = socket?.getInputStream()
                isReconnecting = false
                val registerBytes = TcpRequestUtils.bean2Bytes(TcpSendMsgUtil.instance.sendRegisterWatch())
                printByteArrayAsHex(registerBytes)
                // 发送注册指令
                output?.write(registerBytes)
                output?.flush()
                // 开始循环接收消息
                while (true) {
                    val bufferSize = 1024
                    val buffer = ByteArray(bufferSize)
                    val bytesRead = input?.read(buffer)
                    val message = bytesRead?.let { buffer.copyOfRange(0, it) }
                    //printByteArrayAsHex(message)
                    if (message != null) {
                        listener?.onMessageReceived(message)
                    }
                }
            } catch (e: Exception) {
                MyLog.e(TAG, "TCP connectAndRegister error: $e")
                if (!isReconnecting) {
                    startReconnect() // 断网后开始重连
                }
            }
        }.start()
    }

    private fun printByteArrayAsHex(byteArray: ByteArray) {
        val hexString = StringBuilder()
        byteArray.forEachIndexed { _, b ->
            hexString.append(String.format("%02X ", b))
        }
        MyLog.d(TAG, "printByteArrayAsHex: $hexString")
    }

    fun startHeartbeat() {
        heartbeatTimer?.cancel()
        heartbeatTimer = timer.schedule(0, hearBeatInterval) {
            sendHeartbeat()
        }
    }

    private fun sendHeartbeat() {
        Thread {
            output?.let {
                try {
                    val heartBeatBytes = TcpRequestUtils.bean2Bytes(TcpSendMsgUtil.instance.sendHeartBeat())
                    it.write(heartBeatBytes)
                    it.flush()
                } catch (e: Exception) {
                    MyLog.e(TAG, "TCP sendHeartbeat error: $e, isReconnecting: $isReconnecting")
                    if (!isReconnecting) {
                        startReconnect()
                    }
                }
            }
        }.start()
    }

    fun startReconnect() {
        isReconnecting = true
        reconnectTimer?.cancel()
        // 设置重连定时器
        reconnectTimer = timer.schedule(reconnectInterval) {
            if (isReconnecting) {
                reconnect()
            }
        }
    }

    fun disconnect() {
        isReconnecting = false
        heartbeatTimer?.cancel() // 取消心跳定时器
        reconnectTimer?.cancel() // 取消重连定时器

        socket?.use { s ->
            output?.close()
            input?.close()
            s.close()
        }
    }

    private fun reconnect() {
        socket?.use { s ->
            output?.close()
            input?.close()
            s.close()
        }
        //发送重连消息
        val eventBusBean = EventBusBean()
        eventBusBean.eventType = ConstantLogic.EVENT_RECONNECT_TCP
        EventBus.getDefault().post(eventBusBean)
    }
}