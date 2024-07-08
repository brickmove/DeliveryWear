package com.ciot.deliverywear.bean
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * Created by p'c on 2024/7/8.
 * Description:
 * Encoding: utf-8
 */
class RegisterBeanR {
    //设备编号
    @Expose(serialize = true, deserialize = true)
    @SerializedName("id")
    var id: String? = null

    //unix时间戳
    @Expose(serialize = true, deserialize = true)
    @SerializedName("time")
    var time: Long? = null

    //设备类型
    @Expose(serialize = true, deserialize = true)
    @SerializedName("type")
    var type: Int? = null

    //软件版本号
    @Expose(serialize = true, deserialize = true)
    @SerializedName("version")
    var version: String? = null
}