package com.kgxl.base.ext

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Created by kgxl on 2022/11/10
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


fun Context.getVersionCode(): String {
    return getVersionCode(this).toString()
}

private fun getVersionCode(context: Context): Long {
    val pi = context.packageManager.getPackageInfo(context.packageName, 0)
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        pi?.longVersionCode ?: -1
    } else {
        (pi?.versionCode ?: -1).toLong()
    }
}

fun Context.toMarket(isGoogle: Boolean = true) {
    val intent = Intent(Intent.ACTION_VIEW)
    if (isGoogle) {
        intent.data =
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName") //google
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } else {
        intent.data =
            Uri.parse("market://details?id=$packageName") //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
        if (intent.resolveActivity(packageManager) != null) {
            //可以接收
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.no_store, Toast.LENGTH_SHORT).show()
        }
    }
}

fun Context.toShare() {
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(
            Intent.EXTRA_TEXT,
            "${getString(R.string.share_sudoku)} https://play.google.com/store/apps/details?id=${packageName}"
        )
        type = "text/plain"
    }
    Intent.createChooser(intent, null)
    startActivity(intent)
}

fun Context.toBrowse(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        data = Uri.parse(url)
    }
    startActivity(intent)
}