package com.kgxl.base.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.kgxl.base.BaseBindingActivity
import com.kgxl.base.ext.launch
import com.kgxl.base.test.databinding.ActivityBleBinding
import com.kgxl.ble.isConnect
import kotlinx.coroutines.flow.collectLatest
import no.nordicsemi.android.support.v18.scanner.ScanResult


class BleActivity : BaseBindingActivity<ActivityBleBinding>() {
    var bleManager: MyBleManager? = null
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            bleManager?.let {
                if (activityResult.resultCode == Activity.RESULT_OK || activityResult.resultCode == Activity.RESULT_CANCELED) {
                    it.startScanner(null, it.buildBleSetting())
                }
            }
        }
    private val permissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.filter { !it.value }.isEmpty()) {
                bleManager?.let {
                    it.startScanner(null, it.buildBleSetting())
                }
            }
        }
    private val bles = ArrayList<ScanResult>()
    private val mAdapter by lazy {
        BleAdapter(bles)
    }

    override fun onStop() {
        super.onStop()
        bleManager?.stopScanner()
    }

    override fun initViewBinding(): ActivityBleBinding {
        return ActivityBleBinding.inflate(layoutInflater)
    }

    override fun initView() {
        bleManager = MyBleManager(this, activityResultLauncher, permissions)
        mViewBinding.btnScanner.setOnClickListener {
            bleManager?.let {
                it.startScanner(null, it.buildBleSetting())
            }
        }
        mViewBinding.btnStop.setOnClickListener {
            bleManager?.stopScanner()
        }
        bleManager?.bondList?.forEach {
            println("${it.name} ${it.isConnect()}")
        }
        mViewBinding.rvBle.apply {
            layoutManager = LinearLayoutManager(this@BleActivity)
            adapter = mAdapter
            mAdapter.setOnItemClickListener(object : BleAdapter.OnItemClickListener {
                @SuppressLint("MissingPermission")
                override fun onItemClick(position: Int, result: ScanResult) {
                    if (bleManager?.isConnected == true) {
                        bleManager?.disconnect()
                        bleManager?.startConnect(result.device)
                        Toast.makeText(this@BleActivity, "已经链接", Toast.LENGTH_SHORT).show()
                    } else {
                        bleManager?.startConnect(result.device)
                    }
                }
            })
        }
    }

    override fun initData() {
    }

    override fun initObserver() {
        launch {
            bleManager?.scanResult?.collectLatest {
                bles.clear()
                bles.addAll(it.filter { it.scanRecord?.deviceName != null })
                mAdapter.notifyDataSetChanged()
            }
        }

        launch {
            bleManager?.bondingState?.observe(this@BleActivity) {
                println("bondingStateAsFlow $it")
            }
        }
        launch {
            bleManager?.state?.observe(this@BleActivity) {
                println("stateAsFlow $it")
            }
        }
    }
}