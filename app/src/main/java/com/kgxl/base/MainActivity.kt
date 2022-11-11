package com.kgxl.base

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kgxl.download.DownLoadFactory
import com.kgxl.download.OKDownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator

class MainActivity : AppCompatActivity() {
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kgxl.base.test.R.layout.activity_main)

        findViewById<Button>(com.kgxl.base.test.R.id.btn_send).setOnClickListener {

            NotificationUtil.startDownloadNotify(this, NotificationUtil.DEFAULT_CHANNEL_ID, "下载通知", count++)
        }

        findViewById<Button>(com.kgxl.base.test.R.id.btn_clear).setOnClickListener {

            NotificationUtil.clearNotify(this, NotificationUtil.DEFAULT_CHANNEL_ID)
        }
        findViewById<Button>(com.kgxl.base.test.R.id.btn_download).setOnClickListener {

            DownLoadFactory.getOkDownload().apply {
                start(createTask("https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk", externalCacheDir?.absolutePath
                    ?: ""), true, object : OKDownloadListener<DownloadTask> {
                    override fun onStart(task: DownloadTask) {
                        println("onStart")
                    }

                    override fun onComplete(task: DownloadTask) {
                        println("onComplete")
                    }

                    override fun onError(task: DownloadTask, error: String) {
                        println("onError $error")
                    }

                    override fun onOkProgress(task: DownloadTask, currentLength: Long, speed: SpeedCalculator) {
                        println("onOkProgress $currentLength ${speed.speed()}")
                    }

                    override fun onProgress(currentLength: Long, total: Long) {
                        println("onOkProgress")
                    }
                })
            }
        }

    }

}