package com.kgxl.ble

import android.bluetooth.BluetoothDevice
import java.lang.reflect.InvocationTargetException

/**
 * Created by kgxl on 2022/11/17
 */
fun BluetoothDevice.isConnect(): Boolean {
    try {
        //使用反射调用被隐藏的方法
        val isConnectedMethod =
            BluetoothDevice::class.java.getDeclaredMethod(
                "isConnected"
            )
        isConnectedMethod.isAccessible = true
        return isConnectedMethod.invoke(this) as Boolean
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    }
    return false
}