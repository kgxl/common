package com.kgxl.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat


/**
 * Created by zjy on 2022/11/14
 */
object BlePermissionUtil {

    fun isBluetoothScanAndConnectPermissionsGranted(context: Context): Boolean {
        return if (!isSorAbove()) true else ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationPermissionsGranted(context: Context): Boolean {
        return if (isSorAbove()) true else ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun isEnable(): Boolean {
        return BluetoothAdapter.getDefaultAdapter().isEnabled
    }

    fun isSorAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    private fun isWithinMarshmallowAndR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.R
    }

    private fun isKitkatOrAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    fun checkGPS(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}