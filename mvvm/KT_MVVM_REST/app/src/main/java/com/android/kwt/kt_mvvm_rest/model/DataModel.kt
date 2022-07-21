package com.android.kwt.kt_mvvm_rest.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class GetModel(
    @SerializedName("result")
    val result: String,
    @SerializedName("msg")
    val msg: String
)

data class PostModel(
    @SerializedName("area")
    val area: JsonObject
)

data class PostDrawModel(
    @SerializedName("draw")
    val draw: JsonObject
)
