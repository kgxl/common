package com.kgxl.base.camera

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore


/**
 * Created by kgxl on 2023/1/12
 * 监听相册改变
 * 必须添加[android.Manifest.permission.READ_EXTERNAL_STORAGE]权限否则无法读取
 */
class PhotoContentObserve(val content: Context, private val handler: Handler) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        uri?.let {
            val c = content.contentResolver.query(it, null, null, null, null)
            c?.let {
                if (c.moveToFirst()) {
                    val data = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    println("photo ${data}")
                }
                c.close()
            }
        }
    }
}