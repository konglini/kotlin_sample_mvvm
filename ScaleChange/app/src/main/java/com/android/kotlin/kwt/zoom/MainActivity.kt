package com.android.kotlin.kwt.zoom

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_fixed.setOnClickListener(this)

        initZoom()
    }

    override fun onClick(v: View?) {
        if (v == btn_fixed) {
            if (btn_fixed.text.equals("UNLOCK")) {
                fixed_text = false
                btn_fixed.text = "LOCK"
            } else {
                fixed_text = true
                btn_fixed.text = "UNLOCK"
            }
        }
    }

    var move_x = 0f
    var move_y = 0f
    var fixed_text = false

    fun initZoom() {
        var scaleFactor = 1.0f
        val scaleGestureDetector = ScaleGestureDetector(
            this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scaleFactor *= detector.scaleFactor
                    scaleFactor = scaleFactor.coerceIn(0.5f, 2.5f)

                    tv_hello.scaleX = scaleFactor
                    tv_hello.scaleY = scaleFactor

                    return super.onScale(detector)
                }
            }
        )

        tv_hello.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (!fixed_text) {
                    scaleGestureDetector.onTouchEvent(event)
                }

                if (event!!.action == MotionEvent.ACTION_UP) {
                    tv_hello.setTextColor(Color.BLACK)
                } else if (event!!.action == MotionEvent.ACTION_DOWN) {
                    move_x = v!!.x - event.rawX
                    move_y = v!!.y - event.rawY

                    if (fixed_text) {
                        tv_hello.setTextColor(Color.CYAN)
                    }
                } else if (event!!.action == MotionEvent.ACTION_MOVE) {
                    if (!fixed_text) {
                        v!!.animate()
                            .x(event.rawX + move_x)
                            .y(event.rawY + move_y)
                            .setDuration(0).start()
                    }
                }

                return true
            }
        })
    }
}