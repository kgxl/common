package com.kgxl.download

/**
 * Created by zjy on 2022/11/11
 */
interface DownloadListener<T> {
    fun onStart(task: T)
    fun onProgress(currentLength: Long, total: Long)
    fun onComplete(task: T)
    fun onError(task: T,error:String)
}