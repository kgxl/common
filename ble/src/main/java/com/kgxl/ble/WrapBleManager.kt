package com.kgxl.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import kotlinx.coroutines.flow.MutableSharedFlow
import no.nordicsemi.android.ble.Request
import no.nordicsemi.android.ble.livedata.ObservableBleManager
import no.nordicsemi.android.support.v18.scanner.*
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created by kgxl on 2022/11/14
 */
abstract class WrapBleManager(private val ctx: Context, private val activityResultLauncher: ActivityResultLauncher<Intent>, private val requestPermissions: ActivityResultLauncher<Array<String>>) : ObservableBleManager(ctx) {
    val scanResult = MutableSharedFlow<List<ScanResult>>(0, extraBufferCapacity = 3)
    val bondList = ArrayList<BluetoothDevice>()

    @Volatile
    var scanState = AtomicBoolean(false)
    private val scannerCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            scanState.compareAndSet(false, true)
            println("onScanResult ${result}")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            println("onBatchScanResults ${results.size}")
            scanState.compareAndSet(false, true)
            scanResult.tryEmit(results)
        }

        override fun onScanFailed(errorCode: Int) {
            println("onScanFailed ${errorCode}")
            scanState.compareAndSet(false, true)
        }
    }

    /**
     * 开启扫描
     */
    fun startScanner(filters: List<ScanFilter>?,
                     settings: ScanSettings): Boolean {
        if (BlePermissionUtil.isBluetoothScanAndConnectPermissionsGranted(ctx)) {
            if (BlePermissionUtil.isLocationPermissionsGranted(ctx)) {
                if (!BlePermissionUtil.checkGPS(ctx)) {
                    openGPS()
                } else {
                    if (BlePermissionUtil.isEnable()) {
                        val scannerCompat = BluetoothLeScannerCompat.getScanner()
                        if (scanState.compareAndSet(false, true)) {
                            scannerCompat.startScan(filters, settings, scannerCallback)
                        }
                        return true
                    } else {
                        activityResultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }
                }
            } else {
                requestPermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        } else {
            if (BlePermissionUtil.isSorAbove()) {
                requestPermissions.launch(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT))
            }
        }
        return false
    }

    fun stopScanner() {
        BluetoothLeScannerCompat.getScanner().stopScan(scannerCallback)
        scanState.compareAndSet(true, false)
    }

    fun buildBleSetting(): ScanSettings {
        return ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(5000)
            .setUseHardwareBatchingIfSupported(true)
            .build()
    }

    @SuppressLint("MissingPermission")
    fun getAllBondDevice() {
        if (bondList.isEmpty()) {
            BluetoothAdapter.getDefaultAdapter().bondedDevices.forEach {
                bondList.add(it)
            }
        }
    }

    /**
     * 开始连接蓝牙
     */
    fun startConnect(ble: BluetoothDevice) {
        getAllBondDevice()
        if (bondList.filter { ble.address == it.address }.isEmpty()) {
            startBond()
        } else {
            connect(ble).enqueue()
        }
    }

    /**
     * 获取当前链接设备
     */
    suspend fun getCurrentConnectDevices(): List<BluetoothDevice> {
        getAllBondDevice()
        val devices = ArrayList<BluetoothDevice>()
        bondList.forEach {
            if (it.isConnect()) {
                devices.add(it)
            }
        }
        return devices
    }

    private fun startBond(): Request {
        return createBondInsecure()
    }

    private fun openGPS() {
        activityResultLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}