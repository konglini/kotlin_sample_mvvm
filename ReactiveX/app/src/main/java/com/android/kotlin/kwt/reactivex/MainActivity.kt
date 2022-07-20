package com.android.kotlin.kwt.reactivex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable
            .interval(0, 1, TimeUnit.SECONDS)
            .subscribe {
                runOnUiThread {
                    tv_hello.text = it.toString()
                }
            }

        val item1 = Observable.range(1, 4)
        val item2 = Observable.just("n1", "n2", "n3", "n4")

        val result_item = Observable.zip(item1, item2) { n, s -> "$n $s" }
        result_item.subscribe(::println)

//        val total = item.sum();
//        total.subscribe { s -> println(s) }
    }

    fun Observable<Int>.sum() = reduce(0) { t, n -> t + n }
}