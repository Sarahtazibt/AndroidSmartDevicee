package fr.isen.tazibt.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ScanAdapter(
    var devices: ArrayList<BluetoothDevice>,
    var onDeviceClickListener: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<ScanAdapter.CategoryViewHolder>() {
    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cellName = view.findViewById<TextView>(R.id.test1)
        val rssiName = view.findViewById<TextView>(R.id.textView)

    }
    private var rssiValue : MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ScanAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cellscan, parent, false)


        return CategoryViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.cellName.text = devices[position].name ?: "Inconnu"
        holder.rssiName.text = rssiValue[position].toString()
        holder.itemView.setOnClickListener {
            onDeviceClickListener(devices[position])
        }

    }


    override fun getItemCount() = devices.size
    @SuppressLint("MissingPermission")
    fun addDevice(device: ScanResult) {
        var shouldAddDevice = true
        devices.forEachIndexed { index, bluetoothDevice ->
            if (bluetoothDevice.name == device.device.name) {
                devices[index] = device.device
                shouldAddDevice = false
            }
        }
        if (shouldAddDevice) {
            devices.add(device.device)
            rssiValue.add(device.rssi)
        }

    }

}




