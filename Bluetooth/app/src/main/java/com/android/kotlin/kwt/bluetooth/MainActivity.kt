package com.android.kotlin.kwt.bluetooth

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guru.bluetooth.hm10.BluetoothService
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val PERMISSIONS_LIST = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var activity_result = -1

    var dialogDeviceList: DialogDeviceList? = null

    private var bluetoothService: BluetoothService? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    var bt_address = ""
    var bt_connect = false
    var bt_user_connect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_connect.setOnClickListener(this)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            cancelCurrentAndShowToast(R.string.bt_no_device, Toast.LENGTH_LONG)
            finish()
        } else {
            initBluetooth()
            initPermissions()
            initActivityResult()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bluetoothDevice != null) {
            bluetoothService!!.disconnectGattServer()
        }
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
            unbindService(serviceConnection)
            bluetoothService!!.stopSelf()
        } catch (e: Exception) {
        }
        Process.killProcess(Process.myPid())
    }

    override fun onClick(view: View) {
        if (view === btn_connect) {
            if (!bt_connect) {
                showDialogDeviceList()
                if((btn_connect.text).equals(getString(R.string.connecting))){
                    disconnectedBT()
                    bluetoothService!!.disconnectGattServer()
                }
            } else {
                if (bluetoothDevice != null) {
                    //tv_bt.setText("")
                    disconnectedBT()
                    bluetoothService!!.disconnectGattServer()
                }
            }
        }
        else if(view === btn_bt_on_off){
            setOnBluetooth()
        }
    }

    fun initBluetooth() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter?.isEnabled == false) {
                btn_bt_on_off.isChecked = true
                btn_connect.isVisible = false
            } else {
                btn_bt_on_off.isChecked = false
                btn_connect.isVisible = true
            }
        }
        btn_bt_on_off.setOnCheckedChangeListener { _, isChecked ->
            setOnBluetooth()
        }
    }


    private fun initPermissions() {
        //설명_ 권한 체크
        var checkPermissionsEnabled = false
        val permissions = ArrayList<String>()
        for (i in PERMISSIONS_LIST.indices) {
            if (checkSelfPermission(PERMISSIONS_LIST[i]) !== PackageManager.PERMISSION_GRANTED) {
                checkPermissionsEnabled = true
                permissions.add(PERMISSIONS_LIST[i])
            }
        }
        if (checkPermissionsEnabled) {
            val str_permissions = arrayOfNulls<String>(permissions.size)
            permissions.toArray(str_permissions)
            requestPermissions(str_permissions, REQUEST_ALL_PERMISSION)
        } else {
            service_init()
        }
    }

    fun initActivityResult() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (activity_result == 1) {
                    settingBT()
                    btn_connect.isVisible = true
                }
            }
    }

    private fun service_init() {
        val bindIntent = Intent(this, BluetoothService::class.java)
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, makeGattUpdateIntentFilter())
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            bluetoothService = (rawBinder as BluetoothService.LocalBinder).service
            if (!bluetoothService!!.initialize()) {
                finish()
            }
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            bluetoothService = null
        }
    }

    var log_start = false
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()

            if (action == BluetoothService.ACTION_GATT_CONNECTED) {
                runOnUiThread {
                    bt_connect = true
                    btn_connect!!.setText(R.string.disconnect)
                    if (!log_start) {
                        log_start = true
//                        fileRead()
                    }
                }
            } else if (action == BluetoothService.ACTION_GATT_DISCONNECTED) {
                disconnectedBT()
            }
            if (action == BluetoothService.ACTION_DATA_AVAILABLE) {
                val txValue: ByteArray? = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA)
                runOnUiThread {
                    //메인 스레드에서 데이터 값 가져오기 블투 송신 데이터
                    try {
                        val text = txValue?.let { String(it, charset(Define.CHARSET)) }
                        var bt_data: String? = null

                        bt_data = text
                        Log.i("!---bt_data", bt_data!!)
                    } catch (e: Exception) {
                    }
                }
            }
            if (action == BluetoothService.DEVICE_DOES_NOT_SUPPORT) {
                bluetoothService!!.disconnectGattServer()
            }
        }
    }

    private fun showDialogDeviceList() {
        dialogDeviceList = DialogDeviceList(this)
        dialogDeviceList!!.show()
        dialogDeviceList!!.setOnDismissListener {
            if (dialogDeviceList!!.getResultDevice() != null) {
                bt_address = dialogDeviceList!!.getResultDevice()!!.address

                connectBT()
            }
        }
    }

    private fun connectBT() {
        bt_user_connect = true
        btn_connect!!.setText(R.string.connecting)
        bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bt_address)
        bluetoothService!!.connect(bt_address)
    }

    fun disconnectedBT() {
        runOnUiThread {
            log_start = false
            bt_connect = false
            bt_user_connect = false
            btn_connect!!.setText(R.string.connect)
        }
    }

    private fun settingBT(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //설명_ BT 기능이 꺼져있으면 설정할 수 있는 창을 띄움
        if (bluetoothAdapter?.isEnabled == false) {
            activity_result = 1
            activityResultLauncher.launch(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            )
        } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //설명_ 특정 기기 중 GPS 기능이 켜져있지 않으면 블루투스 스캔이 되지 않아 GPS를 설정할 수 있는 창을 띄움
            val alertDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.permissions_negative))
                .setMessage(R.string.setting_main_text_gps)
                .setCancelable(false)
                .setPositiveButton(
                    R.string.okButton
                ) { dialog, which ->
                    activity_result = 0
                    activityResultLauncher.launch(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:com.android.kotlin.kwt.bluetooth")
                        )
                    )
                }
                .setNegativeButton(
                    R.string.noButton
                ) { dialog, which ->
                    cancelCurrentAndShowToast(
                        R.string.setting_main_text_gps,
                        Toast.LENGTH_SHORT
                    )
                }
                .create()
            alertDialog.show()
        } else {
            return true
        }
        return false
    }

    fun setOnBluetooth() {
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d("bluetoothAdapter", "Device doesn't support Bluetooth")
        } else {
            if (bluetoothAdapter?.isEnabled == false) {
                activity_result = 1
                activityResultLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            } else {
                bluetoothAdapter?.disable()
                btn_connect.isVisible = false
            }
        }
    }

    fun showDialogCheckPermissionsEnabled() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.permissions_negative))
            .setMessage(getString(R.string.permissions_text))
            .setCancelable(false)
            .setNegativeButton(
                getString(R.string.permissions_ok)
            ) { dialog, which ->
                activity_result = 0
                activityResultLauncher.launch(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.android.kotlin.kwt.bluetooth")
                    )
                )
                showToast(
                    resources.getString(R.string.permissions),
                    Toast.LENGTH_SHORT
                )

                finish()
            }
            .setPositiveButton(
                getString(R.string.negative)
            ) { dialog, which ->
                showToast(
                    resources.getString(R.string.permissions_negative_text),
                    Toast.LENGTH_SHORT
                )

                finish()
            }
            .create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                var isAllEnabled = true
                var i = 0
                while (i < permissions.size) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isAllEnabled = false
                    }
                    i++
                }
                if (isAllEnabled) {
                    service_init()
                } else {
                    showDialogCheckPermissionsEnabled()
                }
            }
        }
    }

    fun showToast(msg: String, length: Int) {
        Toast.makeText(
            this,
            msg,
            length
        ).show()
    }

    private var toast: Toast? = null
    private fun cancelCurrentAndShowToast(msg_id: Int, toastShowLength: Int) {
        if (toast != null) {
            toast!!.cancel()
        }
        toast = Toast.makeText(this, msg_id, toastShowLength)
        toast!!.show()
    }

    companion object {
        //설명_ 앱에서 요구하는 권한
        private val PERMISSIONS_LIST = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        private const val REQUEST_ALL_PERMISSION = 1
        private const val REQUEST_BT = 2

        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE)
            intentFilter.addAction(BluetoothService.DEVICE_DOES_NOT_SUPPORT)
            return intentFilter
        }
    }
}