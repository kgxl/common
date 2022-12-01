package com.kgxl.base

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.widget.Button
import com.kgxl.base.ble.BleActivity
import com.kgxl.base.ext.launch
import com.kgxl.base.test.databinding.ActivityMainBinding
import com.kgxl.base.utils.NotificationUtil
import com.kgxl.base.utils.SPUtils
import com.kgxl.ble.BleReceiver
import com.kgxl.download.DownLoadFactory
import com.kgxl.download.OKDownloadListener
import com.kgxl.network.RetrofitHelper
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import java.util.concurrent.TimeUnit

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
    var count = 0

    override fun onResume() {
        super.onResume()
        val bleReceiver = BleReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bleReceiver, intentFilter)
        launch {
            bleReceiver.bleState.collect {
                println("$it")
            }
        }
    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        findViewById<Button>(com.kgxl.base.test.R.id.btn_send).setOnClickListener {

            NotificationUtil.startDownloadNotify(this, NotificationUtil.DEFAULT_CHANNEL_ID, com.kgxl.base.test.R.mipmap.ic_launcher, "下载通知", count++)
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
        mViewBinding.btnBle.setOnClickListener {
            startActivity(Intent(this, BleActivity::class.java))
        }
        mViewBinding.btnNet.setOnClickListener {
            launch {
                RetrofitHelper.resetOkhttpClient()
                RetrofitHelper.setOkhttpClient(RetrofitHelper.getOkhttpClient().newBuilder().readTimeout(10, TimeUnit.SECONDS).build())
                val api = RetrofitHelper.create<TestApi>("http://wwww.baidu.com")
                println(api.getBaidu().toString())
            }
        }
        SPUtils.save("123", 111)
        println("sp get ${SPUtils.get("123", 0)}")

    }

    override fun initData() {
    }

    override fun initObserver() {
    }

}