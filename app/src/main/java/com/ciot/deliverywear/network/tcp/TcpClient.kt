package com.ciot.deliverywear.network.tcp

import android.util.Log
import com.ciot.deliverywear.bean.EventBusBean
import com.ciot.deliverywear.constant.ConstantLogic
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
    private var TAG = ConstantLogic.TCP_TAG
    private lateinit var socket: Socket
    private lateinit var input: InputStream
    private lateinit var output: OutputStream
    private var heartbeatTimer: Timer? = null

    private var reconnectTimer: TimerTask? = null
    private var reconnectInterval: Long = 5000 // 重连间隔，默认为5秒
    private var isReconnecting: Boolean = false // 是否正在重连
    private var listener: TcpClientListener? = null

    private val bufferSize = 1024
    private val buffer = ByteArray(bufferSize)

    companion object {
        private var instance: TcpClient? = null

        fun getInstance(serverIp: String, serverPort: Int): TcpClient {
            if (instance == null) {
                instance = TcpClient(serverIp, serverPort)
            }
            return instance!!
        }
    }

    fun setListener(listener: TcpClientListener) {
        this.listener = listener
    }

    fun connectAndRegister() {
        Thread {
            try {
                socket = Socket(serverIp, serverPort)
                output = socket.getOutputStream()
                val registerBytes = TcpRequestUtils.bean2Bytes(TcpSendMsgUtil.instance.sendRegisterWatch())
                printByteArrayAsHex(registerBytes)
                // 发送注册指令
                output.write(registerBytes)
                output.flush()

                input = socket.getInputStream()
                // 开始循环接收消息
                while (true) {
                    val bytesRead = input.read(buffer)
                    val message = buffer.copyOfRange(0, bytesRead)
                    //printByteArrayAsHex(message)
                    listener?.onMessageReceived(message)
                }

            } catch (e: Exception) {
                Log.e(TAG, "TCP connectAndRegister error: $e")
                if (!isReconnecting) {
                    startReconnect() // 断网后开始重连
                }
            }
        }.start()
    }

    private fun printByteArrayAsHex(byteArray: ByteArray) {
        val hexString = StringBuilder()
        for (byte in byteArray) {
            val hex = String.format("%02X", byte)
            hexString.append(hex).append(" ")
        }
        Log.d(TAG, "printByteArrayAsHex: $hexString")
    }

    fun startHeartbeat() {
        heartbeatTimer = Timer()
        heartbeatTimer!!.schedule(0, 1000) {
            sendHeartbeat()
        }
    }

    private fun sendHeartbeat() {
        Thread {
            if (this::output.isInitialized) {
                try {
                    val heartBeatBytes = TcpRequestUtils.bean2Bytes(TcpSendMsgUtil.instance.sendHeartBeat())
                    output.write(heartBeatBytes)
                    output.flush()

                } catch (e: Exception) {
                    Log.e(TAG, "TCP sendHeartbeat error: $e")
                    if (!isReconnecting) {
                        startReconnect() // 发送心跳失败后开始重连
                    }
                }
            }
        }.start()
    }

    fun startReconnect() {
        isReconnecting = true
        // 设置重连定时器
        reconnectTimer = Timer().schedule(reconnectInterval) {
            if (isReconnecting) {
                reconnect()
            }
        }
    }

    fun disconnect() {
        isReconnecting = false
        heartbeatTimer?.cancel() // 取消心跳定时器
        reconnectTimer?.cancel() // 取消重连定时器

        try {
            socket.close()
            output.close()
            input.close()
        } catch (e: Exception) {
            Log.e(TAG, "TCP disconnect error: $e")
        }
    }

    private fun reconnect() {
        try {
            socket.close()
            output.close()
            input.close()
        } catch (e: Exception) {
            Log.e(TAG, "TCP reconnect error: $e")
        }
        //发送重连消息
        val eventBusBean = EventBusBean()
        eventBusBean.eventType = ConstantLogic.EVENT_RECONNECT_TCP
        EventBus.getDefault().post(eventBusBean)
    }
}