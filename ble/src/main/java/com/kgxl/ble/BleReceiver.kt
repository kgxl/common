package com.kgxl.ble

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow


/**
 * Created by kgxl on 2022/11/14
 * 监听蓝牙打开和链接状态
 */
class BleReceiver : BroadcastReceiver() {
    val bleState = MutableStateFlow<BleState>(BleState.OFF)
    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
        val connectState = intent?.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1)
        when (state) {
            BluetoothAdapter.STATE_TURNING_ON -> bleState.compareAndSet(BleState.OFF, BleState.ON_ING)
            BluetoothAdapter.STATE_ON -> bleState.compareAndSet(BleState.ON_ING, BleState.ON)
            BluetoothAdapter.STATE_TURNING_OFF -> bleState.compareAndSet(BleState.ON, BleState.OFF_ING)
            BluetoothAdapter.STATE_OFF -> bleState.compareAndSet(BleState.OFF_ING, BleState.OFF)
        }
        when (connectState) {
            BluetoothAdapter.STATE_CONNECTED -> bleState.tryEmit(BleState.CONNECTED)
            BluetoothAdapter.STATE_CONNECTING -> bleState.tryEmit(BleState.CONNECT_ING)
            BluetoothAdapter.STATE_DISCONNECTED -> bleState.tryEmit(BleState.DISCONNECTED)
            BluetoothAdapter.STATE_DISCONNECTING -> bleState.tryEmit(BleState.DISCONNECT_ING)
        }
    }

    sealed class BleState {
        object ON : BleState()
        object OFF : BleState()
        object ON_ING : BleState()
        object OFF_ING : BleState()

        object CONNECT_ING : BleState()
        object CONNECTED : BleState()
        object DISCONNECT_ING : BleState()
        object DISCONNECTED : BleState()
    }
}