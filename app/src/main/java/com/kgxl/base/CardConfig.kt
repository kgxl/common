package com.kgxl.base

import android.content.Context
import android.util.TypedValue




/**
 * Created by zjy on 2022/12/2
 */
object CardConfig {
    //屏幕最对同时显示几个item
    var MAX_SHOW_COUNT = 4

    //没一级Scale相差0.05f，translation相差7dp左右
    var SCALE_GAP = 0f
    var TRANS_V_GAP = 0
    fun initConfig(context: Context) {
        MAX_SHOW_COUNT = 4
        SCALE_GAP = 0.05f
        TRANS_V_GAP =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, context.getResources().getDisplayMetrics()).toInt()
    }
}