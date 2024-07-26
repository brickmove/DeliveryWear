package com.ciot.deliverywear.utils
import java.util.UUID

class FormatUtil {
    companion object {

        fun formatLable(lable: String) :String {
            if (lable.isEmpty()) {
                return ""
            }
            var newLable: String = ""
            newLable = when (lable) {
                "空闲" -> "Idle"

                else -> "Idle"
            }
            return newLable
        }

        fun createNid(): String {
            val uuid = UUID.randomUUID()
            val uniqueId = uuid.toString()
            return uniqueId
        }
    }
}