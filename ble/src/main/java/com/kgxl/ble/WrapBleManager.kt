package com.kgxl.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult


/**
 * Created by zjy on 2022/11/14
 */
abstract class WrapBleManager(private val ctx: Context, private val activityResultLauncher: ActivityResultLauncher<Intent>, private val requestPermissions: ActivityResultLauncher<Array<String>>) : BleManager(ctx) {

    private val scannerCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            println("onScanResult ${result}")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            println("onScanResult ${results.size}")
        }

        override fun onScanFailed(errorCode: Int) {
            println("onScanFailed ${errorCode}")
        }
    }

    /**
     * 开启扫描
     */
    fun startScanner(): Boolean {
        if (BlePermissionUtil.isBluetoothScanAndConnectPermissionsGranted(ctx)) {
            if (BlePermissionUtil.isLocationPermissionsGranted(ctx)) {
                if (BlePermissionUtil.isEnable()) {
                    val scannerCompat = BluetoothLeScannerCompat.getScanner()
                    scannerCompat.startScan(scannerCallback)
                    return true
                } else {
                    activityResultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
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
    }

}