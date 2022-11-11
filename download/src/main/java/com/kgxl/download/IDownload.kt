package com.kgxl.download

/**
 * Created by zjy on 2022/11/11
 */
interface IDownload<T> {
    fun init()
    fun start(task: T, isSerial: Boolean, listener: DownloadListener<T>)
    fun startMulti(tasks: List<T>, isSerial: Boolean, listener: DownloadListener<T>)
    fun resume(id: String)
    fun resumeAll()
    fun pause(id: String)
    fun pauseAll()
    fun cancel(id: String)
    fun cancelAll()
    fun createTask(url: String, targetPath: String): T
}