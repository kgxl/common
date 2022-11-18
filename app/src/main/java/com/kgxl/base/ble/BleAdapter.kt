package com.kgxl.base.ble

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kgxl.base.test.databinding.ItemBleBinding
import com.kgxl.ble.isConnect
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.support.v18.scanner.ScanResult

/**
 * Created by zjy on 2022/11/15
 */
class BleAdapter(private val data: List<ScanResult>) : RecyclerView.Adapter<BleAdapter.BleViewHolder>() {

    class BleViewHolder(val viewBinding: ItemBleBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val inflate = ItemBleBinding.inflate(LayoutInflater.from(parent.context))
        return BleViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        holder.viewBinding.bleName.text = "Ble name :${data[position].scanRecord?.deviceName}"
        holder.viewBinding.bleAddress.text = data[position].device.address
        holder.viewBinding.bleBond.text =  when(data[position].device.bondState){
            BluetoothDevice.BOND_BONDED->"已绑定"
            BluetoothDevice.BOND_NONE->"未绑定"
            BluetoothDevice.BOND_BONDING->"绑定中..."
            else->"Unknown"
        }
        holder.viewBinding.bleConnect.text = if(data[position].device.isConnect()) "已连接" else "未连接"
        holder.viewBinding.root.setOnClickListener {
            mOnItemClickListener?.onItemClick(position, data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    var mOnItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, result: ScanResult)
    }
}