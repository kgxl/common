package com.kgxl.base.ext

import java.util.regex.Pattern

/**
 * Created by zjy on 2022/11/14
 */

/**
 * 对字符串中url匹配返回
 * key: 网址
 * value:
 *  Pair
 *      first: startIndex  include
 *      second: endIndex   include
 */
fun String.getUrlByString(): HashMap<String, Pair<Int, Int>> {
    val matches =
        Pattern.compile("([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://|[wW]{3}.|[wW][aA][pP].|[fF][tT][pP].|[fF][iI][lL][eE].)[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")
    val matcher = matches.matcher(this)
    val map = HashMap<String, Pair<Int, Int>>()
    while (matcher.find()) {
        map[matcher.group()] = Pair(matcher.start(), matcher.end())
    }
    return map
}

/**
 * 2019-07-22
 * https://github.com/VincentSit/ChinaMobilePhoneNumberRegex
 * 检查输入手机号是否合法
 * @param phone 输入手机号
 * @return 合法则 true，否则 false
 */
fun checkPhoneNumber(phone: String?): Boolean {
    val regex =
        "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4(?:[14]0\\d{3}|[68]\\d{4}|[579]\\d{2}))\\d{6}$"
    return Pattern.matches(regex, phone)
}