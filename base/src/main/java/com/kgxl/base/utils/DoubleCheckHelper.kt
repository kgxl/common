package com.kgxl.base.utils

/**
 * Created by zjy on 2022/12/2
 */
object DoubleCheckHelper {
    private var lastClickTime = 0L
    val doubleCheck: Boolean
        get() {
            val currentTime = System.currentTimeMillis()
            val diff = currentTime - lastClickTime
            if (diff in 0..1500) {
                return true
            }
            lastClickTime = currentTime
            return false
        }
}