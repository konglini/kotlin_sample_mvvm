package com.android.kwt.kotlin.mvvm.ble.repository

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context.BLUETOOTH_SERVICE
import androidx.lifecycle.MutableLiveData
import com.android.kwt.kotlin.mvvm.ble.MyApplication
import com.android.kwt.kotlin.mvvm.ble.uitl.Event
import java.util.*
import kotlin.concurrent.schedule

class BtRepository {
    private val bluetoothManager: BluetoothManager =
        MyApplication.applicationContext()
            .getSystemService(BLUETOOTH_SERVICE) as BluetoothManager

    private val btAdapter: BluetoothAdapter
        get() = bluetoothManager.adapter

    var scanResults: ArrayList<BluetoothDevice> = ArrayList()

    val isScanning = MutableLiveData(Event(false))
    val isConnect = MutableLiveData(Event(false))
    val isEnabledBT = MutableLiveData(Event(false))
    val deviceList = MutableLiveData<Event<ArrayList<BluetoothDevice>>>()


    private val btScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            if (result != null) {
                addScanResult(result)
            }
        }

        private fun addScanResult(result: ScanResult) {
            val device = result.device
            val deviceAddress = device.address
            val deviceName = device.name

            if (deviceName == null || deviceName.isEmpty()) return

            for (device in scanResults) {
                if (device.address == deviceAddress) return
            }
            scanResults.add(result.device)

            deviceList.postValue(Event(scanResults))
        }
    }

    fun startScan() {
        if (btAdapter == null || !btAdapter.isEnabled) {
            isEnabledBT.postValue(Event(true))
            return
        }

        btAdapter.bluetoothLeScanner.startScan(btScanCallback)

        isScanning.postValue(Event(true))

        Timer("SettingUp", false).schedule(5000) { stopScan() }
    }

    fun stopScan() {
        btAdapter.bluetoothLeScanner.stopScan(btScanCallback)

        isScanning.postValue(Event(false))

        scanResults = ArrayList()
    }
}