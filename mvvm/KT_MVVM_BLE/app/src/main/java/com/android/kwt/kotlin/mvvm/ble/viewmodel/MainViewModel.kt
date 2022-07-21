package com.android.kwt.kotlin.mvvm.ble.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.android.kwt.kotlin.mvvm.ble.repository.BtRepository
import com.android.kwt.kotlin.mvvm.ble.uitl.Event
import java.util.*

class MainViewModel : ViewModel() {

    private val repositoryBle = BtRepository()

    val _isScanning: LiveData<Event<Boolean>>
        get() = repositoryBle.isScanning
    val isScanning = ObservableBoolean(false)

    val _isConnect: LiveData<Event<Boolean>>
        get() = repositoryBle.isConnect
    val isConnect = ObservableBoolean(false)

    val isEnabledBT: LiveData<Event<Boolean>>
        get() = repositoryBle.isEnabledBT

    val deviceList: LiveData<Event<ArrayList<BluetoothDevice>>>
        get() = repositoryBle.deviceList

    /**
     *  Start BLE Scan
     */
    fun onClickScan() {
        repositoryBle.startScan()
    }

    fun onClickDisconnect() {
    }

    fun connectDevice(bluetoothDevice: BluetoothDevice) {
    }

    override fun onCleared() {
        super.onCleared()
    }
}