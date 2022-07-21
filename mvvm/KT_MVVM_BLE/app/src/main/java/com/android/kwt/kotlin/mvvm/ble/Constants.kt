package com.android.kwt.kotlin.mvvm.ble

import android.Manifest
import java.util.*

// used to identify adding bluetooth names
const val REQUEST_ENABLE_BT = 1
// used to request fine location permission
const val REQUEST_ALL_PERMISSION = 2
val PERMISSIONS_LIST = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION
)

//사용자 BLE UUID Service/Rx/Tx
val UUID_SERVICE = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
val UUID_TX_CHAR = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
val UUID_RX_CHAR = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

//BluetoothGattDescriptor 고정
val UUID_CLIENT_CHAR_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
