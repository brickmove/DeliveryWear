package com.ciot.deliverywear.network.tcp

import android.util.Log
import com.ciot.deliverywear.constant.ConstantLogic
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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
    private var TAG = ConstantLogic.NETWORK_TAG
    private lateinit var socket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter
    private lateinit var heartbeatTimer: Timer

    private var reconnectTimer: TimerTask? = null
    private var reconnectInterval: Long = 5000 // 重连间隔，默认为5秒
    private var isReconnecting: Boolean = false // 是否正在重连
    private var listener: TcpClientListener? = null

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
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                val registerBytes = TcpRequestUtils.bean2Bytes(TcpSendMsgUtil.instance.sendRegisterWatch())
                // 发送注册指令
                writer.write(String(registerBytes))
                writer.newLine()
                writer.flush()

                // 等待注册成功回复
//                val response = reader.readLine()
//                val receive = TcpRequestUtils.bytes2Bean(response.toByteArray())
//                if (receive.cmd == NetConstant.CONTROL_DEVICE_MANAGEMENT_REGISTER) {
//                    val response: ResultBean = receive.body as ResultBean
//                    if (response.result == true) {
//                        startHeartbeat() // 注册成功后开始发送心跳指令
//                    } else {
//                        startReconnect()
//                    }
//                }

                // 开始循环接收消息
                while (true) {
                    val message = reader.readLine()
                    listener?.onMessageReceived(message)
                }

            } catch (e: Exception) {
                Log.e(TAG, "")
                if (!isReconnecting) {
                    startReconnect() // 断网后开始重连
                }
            }
        }.start()
    }

    fun startHeartbeat() {
        heartbeatTimer = Timer()
        heartbeatTimer.schedule(0, 5000) {
            sendHeartbeat()
        }
    }

    private fun sendHeartbeat() {
        Thread {
            if (this::writer.isInitialized) {
                try {
                    val heartBeatBytes = TcpRequestUtils.bean2Bytes(TcpSendMsgUtil.instance.sendHeartBeat())
                    writer.write(String(heartBeatBytes))
                    writer.newLine()
                    writer.flush()

                } catch (e: Exception) {
                    e.printStackTrace()
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
        heartbeatTimer.cancel() // 取消心跳定时器
        reconnectTimer?.cancel() // 取消重连定时器

        try {
            socket.close()
            reader.close()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun reconnect() {
        try {
            socket.close()
            reader.close()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 重新连接服务器
        connectAndRegister()
    }
}