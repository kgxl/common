package com.kgxl.base.ext

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Created by zjy on 2022/11/10
 */
fun Context.copyText(info: String) {
    copyText(info, true)
}

fun Context.copyText(info: String, isToast: Boolean = false) {
    //获取剪贴板管理器：
    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    // 创建普通字符型ClipData
    val mClipData = ClipData.newPlainText("Label", info)
    // 将ClipData内容放到系统剪贴板里。
    cm.setPrimaryClip(mClipData)
    if (isToast) {
//        ToastUtil.success(this, this.getString(R.string.copy_2_pasteboard))
    }
}


fun Context?.isFinishing(): Boolean {
    if (this == null) {
        return false
    }
    if (this !is Activity) {
        return false
    }
    return this.isFinishing
}