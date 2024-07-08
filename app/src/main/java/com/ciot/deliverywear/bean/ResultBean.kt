package com.ciot.deliverywear.bean
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * Created by p'c on 2024/7/8.
 * Description:
 * Encoding: utf-8
 */
class ResultBean {
    @Expose(serialize = true, deserialize = true)
    @SerializedName("result")
    var result: Boolean? = null

    @Expose(serialize = true, deserialize = true)
    @SerializedName("time")
    var time: String? = null
}