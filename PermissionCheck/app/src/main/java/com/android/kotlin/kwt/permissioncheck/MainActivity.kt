package com.android.kotlin.kwt.permissioncheck

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_LIST = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val REQCODE_REQUEST_PERMISSIONS = 1

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        initPermissions()
    }

    private fun initPermissions() {
        //설명_ 권한 체크
        var check_permissions = false
        val permissions = ArrayList<String>()
        for (i in PERMISSIONS_LIST.indices) {
            if (checkSelfPermission(PERMISSIONS_LIST[i]) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    || PERMISSIONS_LIST[i] != Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) {
                    check_permissions = true
                    permissions.add(PERMISSIONS_LIST[i])
                }
            }
        }
        if (check_permissions) {
            val re_check_permissions = arrayOfNulls<String>(permissions.size)
            permissions.toArray(re_check_permissions)
            requestPermissions(
                re_check_permissions,
                REQCODE_REQUEST_PERMISSIONS
            )
        } else {
            //권한을 모두 허락받았으므로 다음 단계로 이동합니다.
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
                activityResultLauncher.launch(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.android.kotlin.kwt.bluetooth")
                    )
                )
                ShowToast(
                    resources.getString(R.string.permissions),
                    Toast.LENGTH_SHORT
                )

                finish()
            }
            .setPositiveButton(
                getString(R.string.negative)
            ) { dialog, which ->
                ShowToast(
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
            REQCODE_REQUEST_PERMISSIONS -> {
                var isAllEnabled = true
                var i = 0
                while (i < permissions.size) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isAllEnabled = false
                    }
                    i++
                }
                if (isAllEnabled) {
                } else {
                    showDialogCheckPermissionsEnabled()
                }
            }
        }
    }

    fun ShowToast(msg: String, length: Int) {
        Toast.makeText(
            this,
            msg,
            length
        ).show()
    }
}