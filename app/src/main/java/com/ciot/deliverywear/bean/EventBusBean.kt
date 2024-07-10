package com.ciot.deliverywear.bean

import android.os.Bundle

class EventBusBean {
    var eventType: String? = null
    var contentPosition: Int = 0
    var content: String? = null
    var bundle: Bundle? = null

    override fun toString(): String {
        return "EventBusBean{" +
                "eventType=" + eventType +
                ", contentPosition=" + contentPosition +
                ", content='" + content + '\''.toString() +
                '}'.toString()
    }
}
