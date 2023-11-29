package com.kgxl.base.ext

import com.kgxl.base.utils.ScreenUtil

/**
 * Created by kgxl on 2022/11/11
 */
/**
 * dp 2 px
 */
fun Int.dp(): Int {
    return ScreenUtil.dip2px(this.toFloat())
}

/**
 * sp 2 px
 */
fun Int.sp(): Int {
    return ScreenUtil.sp2px(this.toFloat())
}