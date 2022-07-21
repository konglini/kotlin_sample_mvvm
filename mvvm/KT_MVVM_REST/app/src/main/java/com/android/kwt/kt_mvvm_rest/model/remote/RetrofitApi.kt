package com.android.kwt.kt_mvvm_rest.model.remote

import com.android.kwt.kt_mvvm_rest.model.GetModel
import com.android.kwt.kt_mvvm_rest.model.PostDrawModel
import com.android.kwt.kt_mvvm_rest.model.PostModel
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {
    @Headers("Content-Type: application/json")
    @POST("/set_area")
    suspend fun setArea(@Body data: PostModel): Response<GetModel>

    @Headers("Content-Type: application/json")
    @POST("/set_draw")
    suspend fun setDraw(@Body data: PostDrawModel): Response<GetModel>
}