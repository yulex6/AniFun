package com.example.anifun.utils


/**
 * 返回播放量和弹幕数的字符串
 */
fun calString(num: Long?): String {
    val prePoint = num?.div(10000)
    if (prePoint != null) {
        return if (prePoint > 0) {
            val afterPoint = (num % 10000).div(1000)
            "$prePoint.$afterPoint" + "万"
        } else {
            "$num"
        }
    }
    return ""
}

/**
 * 返回时间的字符串
 */
fun calTime(time: Long?): String {
    val min = time?.div(60)
    val sec = time?.minus(min?.times(60) ?: 0)
    return if (min != null && sec != null) {
        if (min < 10 && sec < 10) {
            "0$min:0$sec"
        } else if (sec < 10) {
            "$min:0$sec"
        } else if (min < 10) {
            "0$min:$sec"
        } else {
            "$min:$sec"
        }
    } else {
        ""
    }
}