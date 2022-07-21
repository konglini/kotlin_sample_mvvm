package com.android.kotlin.kwt.bluetooth

import java.util.*


object Define {
    const val CHARSET = "UTF-8"
    val UUID_CLIENT_CHAR_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        val UUID_SERVICE = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
        val UUID_TX_CHAR = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
        val UUID_RX_CHAR = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
//    val UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
//    val UUID_TX_CHAR = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
//    val UUID_RX_CHAR = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    const val bt_cmd_a = "\$CM0,P,0,100,0,0,\r\n"
    const val bt_cmd_b = "\$CM0,P,0,-100,0,0,\\r\\n"
    const val bt_cmd_c = "\$CM0,F,1,8,1,8,\\r\\n"
    const val bt_cmd_d = "\$CM0,B,1,8,1,8,\\r\\n"
    const val bt_cmd_e = "\$CM0,S,0,0,0,0,\\r\\n"
    const val bt_cmd_f = "\$CM0,S,0,0,0,0,\\r\\n"
    const val bt_cmd_g = "tp=c|g"
    const val bt_cmd_h = "tp=c|h"
    const val bt_cmd_i = "tp=c|i"
    const val bt_cmd_j = "tp=c|j"
    const val bt_cmd_k = "tp=c|k"
    const val bt_cmd_l = "tp=c|l"
    const val bt_cmd_m = "tp=c|m"
    const val bt_cmd_n = "tp=c|n"
    const val bt_cmd_o = "tp=c|o"
    const val bt_cmd_p = "tp=c|p"
    const val bt_cmd_q = "tp=c|q"
    const val bt_cmd_r = "tp=c|r"
    const val bt_cmd_s = "tp=s|s"
    const val bt_cmd_t = "tp=c|t"
    const val bt_cmd_u = "tp=c|u"
    const val bt_cmd_v = "tp=c|v"
    const val bt_cmd_w = "tp=c|w"
    const val bt_cmd_x = "tp=c|x"
    const val bt_cmd_y = "tp=c|y"
    const val bt_cmd_z = "tp=c|z"
    const val bt_cmd_0 = "tp=c|0"
    const val bt_cmd_1 = "tp=c|1"
    const val bt_cmd_2 = "tp=c|2"
    const val bt_cmd_3 = "tp=c|3"
    const val bt_cmd_4 = "tp=c|4"
    const val bt_cmd_5 = "tp=c|5"
    const val bt_cmd_6 = "tp=c|6"
    const val bt_cmd_7 = "tp=c|7"
    const val bt_cmd_8 = "tp=c|8"
    const val bt_cmd_9 = "tp=c|9"
    const val bt_cmd_sv = "tp=s|v"
    const val SETTING_ENABLE = "1"
    const val SETTING_DISABLE = "0"
    const val BLUETOOTH_DELAY = 1200
    const val BLUETOOTH_DELAY_HALF = BLUETOOTH_DELAY / 2
    const val BLUETOOTH_DELAY_TRAY = BLUETOOTH_DELAY * 6
}
