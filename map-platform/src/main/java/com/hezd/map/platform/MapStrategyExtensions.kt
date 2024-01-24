package com.hezd.map.platform

/**
 * @author hezd
 * @date 2024/1/12 14:14
 * @description
 */

/**
 * 时间转化
 * @param 秒数
 */
fun formatSToHMStr(second: Int): String {
    val hour = second / 3600
    val minute = second % 3600 / 60
    return if (hour > 0) {
        hour.toString() + "小时" + minute + "分钟"
    } else minute.toString() + "分钟"
}