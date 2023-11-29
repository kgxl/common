package com.kgxl.base.ext

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

/**
 * Created by kgxl on 2023/1/12
 */
fun Uri.getPath(ctx: Context): String {
    val c = ctx.contentResolver.query(this, null, null, null, null)
    try {
        c?.let {
            if (c.moveToFirst()) {
                return c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            }
        }
    } catch (e: Exception) {
        return ""
    } finally {
        c?.close()
    }
    return ""
}