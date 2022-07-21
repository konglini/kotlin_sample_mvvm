package com.android.kwt.kt_mvvm_rest.model.repository

import com.android.kwt.kt_mvvm_rest.model.GetModel
import com.android.kwt.kt_mvvm_rest.model.PostDrawModel
import com.android.kwt.kt_mvvm_rest.model.PostModel
import com.android.kwt.kt_mvvm_rest.model.remote.RetrofitInstance
import retrofit2.Response

class Repository {
    suspend fun setArea(data: PostModel): Response<GetModel> {
        return RetrofitInstance.api.setArea(data)
    }

    suspend fun setDraw(data: PostDrawModel): Response<GetModel> {
        return RetrofitInstance.api.setDraw(data)
    }
}