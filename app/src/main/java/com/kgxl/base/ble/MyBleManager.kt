package com.kgxl.base.ble

import android.bluetooth.BluetoothGatt
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.kgxl.ble.WrapBleManager

/**
 * Created by zjy on 2022/11/14
 */
class MyBleManager(ctx: Context, activityLaunch: ActivityResultLauncher<Intent>, permissions: ActivityResultLauncher<Array<String>>) : WrapBleManager(ctx, activityLaunch, permissions) {

    override fun getGattCallback(): BleManagerGattCallback {
        return object : BleManagerGattCallback() {
            override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
                println("isRequiredServiceSupported")
                return true
            }

            override fun onServicesInvalidated() {
            }


            override fun initialize() {
                super.initialize()
            }

        }
    }


}