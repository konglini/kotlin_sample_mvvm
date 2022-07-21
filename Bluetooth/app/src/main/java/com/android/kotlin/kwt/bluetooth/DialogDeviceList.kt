package com.android.kotlin.kwt.bluetooth

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_device_list.*
import java.util.*


class DialogDeviceList(context: Context) : Dialog(context) {
    init {
//        setCancelable(false)
    }

    private var bluetoothAdapter: BluetoothAdapter? = null

    private var devices_list = ArrayList<BluetoothDevice>()
    private var devices_rssi = ArrayList<Int>()
    private var result_device: BluetoothDevice? = null

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner

    var stop_scan_timer: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_device_list)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay

        val params: ViewGroup.LayoutParams = this.window!!.attributes
        params.width = (display.width * 0.6).toInt()

        this.window!!.attributes = params as WindowManager.LayoutParams

        initDeviceList()
        initBluetooth()
    }

    fun initDeviceList() {
        viewManager = LinearLayoutManager(this.context)
        recyclerViewAdapter = RecyclerViewAdapter(devices_list, devices_rssi)
//        recyclerViewAdapter.mListener = object : RecyclerViewAdapter.OnItemClickListener {
//            override fun onClick(view: View, position: Int) {
//                result_device = devices_list[position]
//
//                dismiss()
//            }
//        }

        rv_bt_list.layoutManager = viewManager
        rv_bt_list.adapter = recyclerViewAdapter
    }

    fun initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner

        btn_scan.setOnClickListener {
            checkBluetooth()
        }
    }

    fun checkBluetooth() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter?.isEnabled == false) {
                context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } else {
                scanBT(true)
            }
        }
    }

    fun scanBT(start: Boolean) {
        bluetoothLeScanner.stopScan(scanCallback)

        if (start) {
            devices_list.clear()
            devices_rssi.clear()
            stop_scan_timer = System.currentTimeMillis() + 5000

            bluetoothLeScanner.startScan(scanCallback)
        }
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            result?.let {
                // result is not null
                if (!devices_list.contains(it.device) && it.device.name != null) {
                    devices_list.add(it.device)
                    devices_rssi.add(it.rssi)

                    recyclerViewAdapter.notifyDataSetChanged()
                    
                    Log.i("!---a", result.device.name)
                }

                if (stop_scan_timer < System.currentTimeMillis()) {
                    scanBT(false)
                }
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            for (result in results) {
                if (!devices_list.contains(result.device) && result.device.name != null) {
                    devices_list.add(result.device)
                    devices_rssi.add(result.rssi)
                    if(!devices_list.contains(result_device)){

                    }

                    recyclerViewAdapter.notifyDataSetChanged()
                }
                Log.i("!---b", result.device.address)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("!---c", "$errorCode")
        }
    }

    class RecyclerViewAdapter(
        private val myDataset: ArrayList<BluetoothDevice>,
        private val rssi: ArrayList<Int>
    ) :
        RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

        var mListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onClick(view: View, position: Int)
        }

        class MyViewHolder(val linearView: LinearLayout) : RecyclerView.ViewHolder(linearView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {
            // create a new view
            val linearView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_device, parent, false) as LinearLayout
            return MyViewHolder(linearView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val clay_item: ConstraintLayout = holder.linearView.findViewById(R.id.clay_item)
            val txt_name: TextView = holder.linearView.findViewById(R.id.txt_name)
            val txt_address: TextView = holder.linearView.findViewById(R.id.txt_address)
            val txt_rssi: TextView = holder.linearView.findViewById(R.id.txt_rssi)

            txt_name.text = myDataset[position].name
            txt_address.text = myDataset[position].address
            txt_rssi.text = rssi[position].toString()
            if (mListener != null) {
                holder?.itemView?.setOnClickListener { v ->
                    mListener?.onClick(v, position)
                }
            }
        }

        override fun getItemCount() = myDataset.size
    }

    fun getResultDevice(): BluetoothDevice? {
        return result_device
    }
}