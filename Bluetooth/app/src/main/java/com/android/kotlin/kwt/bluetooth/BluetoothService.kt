package com.guru.bluetooth.hm10

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.kotlin.kwt.bluetooth.Define


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
class BluetoothService : Service() {
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null

    private var mBtConnectionStateFlag = 0

    private var gatt_start = false

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            gatt_start = true
            Log.i(TAG, "setBTConnect - status: $status / newState: $newState")

            //설명_ 연속적으로 같은 값이 들어오면 중복 진행을 막음
            if (mBtConnectionStateFlag == newState && BluetoothProfile.STATE_CONNECTED == newState) {
                return
            }
            mBtConnectionStateFlag = newState
            if (newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothAdapter.STATE_DISCONNECTED) {
                gatt.discoverServices()
                broadcastUpdate(ACTION_GATT_CONNECTED)
            } else {
                disconnectGattServer()
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                enableTXNotification()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)

        val data: ByteArray? = characteristic.value
        if (data?.isNotEmpty() == true) {
            val hexString: String = data.joinToString(separator = " ") {
                String.format("%02X", it)
            }
            intent.putExtra(EXTRA_DATA, "$data\n$hexString")

            val text = data?.let { String(it, charset(Define.CHARSET)) }
            //Log.i("!---", "$data\n$hexString\n$text")
        }

        // This is handling for the notification on TX Character of NUS service
        if (Define.UUID_TX_CHAR == characteristic.uuid) {
            intent.putExtra(EXTRA_DATA, characteristic.value)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    inner class LocalBinder : Binder() {
        val service: BluetoothService
            get() = this@BluetoothService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        disconnectGattServer()
        return super.onUnbind(intent)
    }

    private val mBinder: IBinder = LocalBinder()
    fun initialize(): Boolean {
        if (mBluetoothManager == null) {
            mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            if (mBluetoothManager == null) {
                return false
            }
        }
        mBluetoothAdapter = mBluetoothManager!!.adapter
        return mBluetoothAdapter != null
    }

    var handler_bt_connection = Handler()
    private fun RunOnBTConnectionThread(runnable: Runnable, delay: Long) {
        handler_bt_connection.removeMessages(0)
        handler_bt_connection.postDelayed(runnable, delay)
    }

    fun connect(address: String?): Boolean {
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "setBTConnect: mBluetoothAdapter == null")
            mBluetoothAdapter = mBluetoothManager!!.adapter
        } else if (address == null || address.isEmpty()) {
            return false
        }

        val device: BluetoothDevice
        try {
            device = mBluetoothAdapter!!.getRemoteDevice(address)
            disconnectGattServer()
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback)
        } catch (e: Exception) {
            broadcastUpdate(ACTION_GATT_DISCONNECTED)
            e.printStackTrace()
            return false
        }

        //설명_ BluetoothGattCallback이 동작이 안되면 다시 연결
        if (!gatt_start) {
            RunOnBTConnectionThread({
                if (!gatt_start) {
                    broadcastUpdate(ACTION_GATT_DISCONNECTED)
                }
            }, (Define.BLUETOOTH_DELAY * 10).toLong())
        }

        Log.i(TAG, "setBTConnect : $address")
        return true
    }

    // 블루투스 해제 함수
    fun disconnectGattServer() {
        Log.d(TAG, "Closing Gatt connection")
        // disconnect and close the gatt
        if (mBluetoothGatt != null) {
            mBluetoothGatt!!.disconnect()
            Handler().postDelayed({
                mBluetoothGatt!!.close()
                mBluetoothGatt = null
                gatt_start = false
            }, 1000) // 동시 처리 시 문제 발생 > 딜레이 처리
        }
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic?) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.readCharacteristic(characteristic)
    }

    fun enableTXNotification() {
        val RxService = mBluetoothGatt!!.getService(Define.UUID_SERVICE) ?: return
        val TxChar = RxService.getCharacteristic(Define.UUID_TX_CHAR) ?: return
        mBluetoothGatt!!.setCharacteristicNotification(TxChar, true)
        val descriptor = TxChar.getDescriptor(Define.UUID_CLIENT_CHAR_CONFIG)
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        mBluetoothGatt!!.writeDescriptor(descriptor)
    }

    fun writeRXCharacteristic(value: ByteArray?) {
        if (mBluetoothGatt == null) {
            return
        }
        val RxService = mBluetoothGatt!!.getService(Define.UUID_SERVICE) ?: return
        val RxChar = RxService.getCharacteristic(Define.UUID_RX_CHAR) ?: return
        RxChar.value = value
        //RxChar.setValue(value)
        mBluetoothGatt!!.writeCharacteristic(RxChar)
    }

    fun writeRXCharacteristic(value: String?) {
        if (mBluetoothGatt == null) {
            return
        }
        val RxService = mBluetoothGatt!!.getService(Define.UUID_SERVICE) ?: return
        val RxChar = RxService.getCharacteristic(Define.UUID_RX_CHAR) ?: return
        //RxChar.value = value
        RxChar.setValue(value)
        mBluetoothGatt!!.writeCharacteristic(RxChar)
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after `BluetoothGatt#discoverServices()` completes successfully.
     *
     * @return A `List` of supported services.
     */
    val supportedGattServices: List<BluetoothGattService>?
        get() = if (mBluetoothGatt == null) null else mBluetoothGatt!!.services

    companion object {
        private val TAG = BluetoothService::class.java.simpleName
        const val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "EXTRA_DATA"
        const val DEVICE_DOES_NOT_SUPPORT = "DEVICE_DOES_NOT_SUPPORT"
    }
}