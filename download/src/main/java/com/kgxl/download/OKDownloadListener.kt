package com.kgxl.download

import com.liulishuo.okdownload.SpeedCalculator

/**
 * Created by zjy on 2022/11/11
 */
interface OKDownloadListener<T> : DownloadListener<T> {
    fun onOkProgress(task: T, currentLength: Long, speed: SpeedCalculator)
}