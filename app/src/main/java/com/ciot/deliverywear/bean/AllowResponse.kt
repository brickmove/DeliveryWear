package com.ciot.deliverywear.bean

/**
 * Created by p'c on 2024/7/8.
 * Description:
 * Encoding: utf-8
 */
class AllowResponse {
    private var success: Boolean = false
    private var data: DataBean? = null
    private var message: String? = null

    fun isSuccess(): Boolean {
        return success
    }

    fun setSuccess(success: Boolean) {
        this.success = success
    }

    fun getData(): DataBean? {
        return data
    }

    fun setData(data: DataBean) {
        this.data = data
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    class DataBean {
        /**
         * host : 接入地址如（127.0.0.1格式的ip地址）
         */
        private var host: String? = null

        /**
         * 接入域名
         */
        private var domain: String? = null

        fun getHost(): String? {
            return this.host
        }

        fun setHost(host: String) {
            this.host = host
        }


        fun getDomain(): String? {
            return domain
        }

        fun setDomain(domain: String) {
            this.domain = domain
        }

    }
}