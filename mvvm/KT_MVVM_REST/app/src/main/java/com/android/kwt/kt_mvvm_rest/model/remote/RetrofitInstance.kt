package com.android.kwt.kt_mvvm_rest.model.remote

import com.android.kwt.kt_mvvm_rest.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: RetrofitApi by lazy {
        retrofit.create(RetrofitApi::class.java)
    }
}