package com.android.kotlin.kwt.bluetooth

import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri

class MediaScanner(ctx: Context?) : MediaScannerConnectionClient {
    private val mScanner: MediaScannerConnection
    private var mFilePaths: Array<String>? = null
    fun startScan(filePaths: Array<String>?) {
        if (filePaths == null) {
            return
        }
        mFilePaths = filePaths
        mScanner.connect() // onMediaScannerConnected()는 connect() 이후 호출됨
    }

    override fun onMediaScannerConnected() {
        if (mFilePaths != null) {
            for (filePath in mFilePaths!!) {
                mScanner.scanFile(filePath, null) // MediaStore 정보 업데이트
            }
        } else {
            mScanner.disconnect() //스캔이 안된경우 연결 해제
        }
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        mScanner.disconnect() // 스캔이 완료되면 연결 해제
    }

    init {
        mScanner = MediaScannerConnection(ctx, this)
    }
}