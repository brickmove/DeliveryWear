package com.ciot.deliverywear.bean
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * Created by p'c on 2024/7/8.
 * Description:
 * Encoding: utf-8
 */
class ArrivedBean {
    @Expose(serialize = true, deserialize = true)
    @SerializedName("time")
    private var time: Int? = null

    @Expose(serialize = true, deserialize = true)
    @SerializedName("nid")
    private var nid: String? = null

    @Expose(serialize = true, deserialize = true)
    @SerializedName("status")
    private var status: Status? = null

    fun getTime(): Int? {
        return time
    }

    fun getNid(): String? {
        return nid
    }

    fun getStatus(): Status? {
        return status
    }

    inner class Status {
        @Expose(serialize = true, deserialize = true)
        @SerializedName("success")
        private var success: Boolean? = null

        @Expose(serialize = true, deserialize = true)
        @SerializedName("time")
        private var time: String? = null

        @Expose(serialize = true, deserialize = true)
        @SerializedName("cost")
        private var cost: String? = null

        @Expose(serialize = true, deserialize = true)
        @SerializedName("name")
        private var name: String? = null

        fun getSuccess(): Boolean? {
            return success
        }

        fun getName(): String? {
            return name
        }
    }
}