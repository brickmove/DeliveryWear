package com.ciot.deliverywear.utils


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
    }
}