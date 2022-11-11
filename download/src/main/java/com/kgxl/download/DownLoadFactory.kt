package com.kgxl.download

import com.liulishuo.okdownload.DownloadTask


/**
 * Created by zjy on 2022/11/11
 */
object DownLoadFactory {
    fun getOkDownload(): IDownload<DownloadTask> {
        return OkDownload.getInstance()
    }
}