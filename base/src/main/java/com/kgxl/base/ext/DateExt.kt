package com.kgxl.base.ext

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zjy on 2022/11/14
 */

fun Long?.convertToTime(formatPattern: String = "yyyy-MM-dd HH:mm:ss", locale: Locale = Locale.getDefault()): String {
    if (this == null) {
        return ""
    }
    val date = Date(this)
    try {
        val format = SimpleDateFormat(formatPattern, locale)
        return format.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun getTimeShowDataString(milliseconds: Long, needNextWeek: Boolean): String? {
    val dataString: String
    val currentTime = Date(milliseconds)
    val today = Date()
    val todayStart = Calendar.getInstance()
    todayStart[Calendar.HOUR_OF_DAY] = 0
    todayStart[Calendar.MINUTE] = 0
    todayStart[Calendar.SECOND] = 0
    todayStart[Calendar.MILLISECOND] = 0
    val todaybegin = todayStart.time
    val yesterdaybegin = Date(todaybegin.time - 3600 * 24 * 1000)
    val preyesterday = Date(yesterdaybegin.time - 3600 * 24 * 1000)
    val tomorrow = Date(todaybegin.time + 3600 * 24 * 1000)
    dataString = if (!currentTime.before(todaybegin) && currentTime.before(tomorrow)) {
        "今天"
    } else if (!currentTime.before(yesterdaybegin) && currentTime.before(todaybegin)) {
        "昨天"
    } else if (!currentTime.before(preyesterday) && currentTime.before(yesterdaybegin)) {
        "前天"
    } else if (needNextWeek || isSameWeekDates(currentTime, today)) {
        getWeekOfDate(currentTime)
    } else {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormatter.format(currentTime)
    }
    return dataString
}

/**
 * 判断两个日期是否在同一周
 */
fun isSameWeekDates(date1: Date?, date2: Date?): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = date1
    cal2.time = date2
    val subYear = cal1[Calendar.YEAR] - cal2[Calendar.YEAR]
    if (0 == subYear) {
        if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
    } else if (1 == subYear && 11 == cal2[Calendar.MONTH]) {
        // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
        if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
    } else if (-1 == subYear && 11 == cal1[Calendar.MONTH]) {
        if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
    }
    return false
}

/**
 * 根据日期获得星期
 *
 * @param date
 * @return
 */
fun getWeekOfDate(date: Date?): String {
    val weekDaysName = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
    // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    calendar.time = date
    val intWeek = calendar[Calendar.DAY_OF_WEEK] - 1
    return weekDaysName[intWeek]
}


/**
 * 转换时间 格式为：00:00:00
 */
fun Long.getCountTimeByLong(): String {
    var totalTime = (this / 1000).toInt() //秒
    var hour = 0
    var minute = 0
    var second = 0
    if (3600 <= totalTime) {
        hour = totalTime / 3600
        totalTime -= 3600 * hour
    }
    if (60 <= totalTime) {
        minute = totalTime / 60
        totalTime -= 60 * minute
    }
    if (0 <= totalTime) {
        second = totalTime
    }
    val sb = StringBuilder()
    if (hour < 10) {
        sb.append("0").append(hour).append(":")
    } else {
        sb.append(hour).append(":")
    }
    if (minute < 10) {
        sb.append("0").append(minute).append(":")
    } else {
        sb.append(minute).append(":")
    }
    if (second < 10) {
        sb.append("0").append(second)
    } else {
        sb.append(second)
    }
    return sb.toString()
}