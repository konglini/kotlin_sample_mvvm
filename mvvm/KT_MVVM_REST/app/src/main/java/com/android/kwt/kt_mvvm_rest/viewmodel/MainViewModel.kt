package com.android.kwt.kt_mvvm_rest.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.kwt.kt_mvvm_rest.model.GetModel
import com.android.kwt.kt_mvvm_rest.model.PostDrawModel
import com.android.kwt.kt_mvvm_rest.model.PostModel
import com.android.kwt.kt_mvvm_rest.model.repository.Repository
import com.android.kwt.kt_mvvm_rest.uitl.Event
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _cctv_num = MutableLiveData<Event<Int>>()
    val cctv_num: LiveData<Event<Int>> get() = _cctv_num

    val set_latitude = MutableLiveData<String>()
    val set_longitude = MutableLiveData<String>()

    private val _snackbar_msg = MutableLiveData<Event<String>>()
    val snackbar_msg: LiveData<Event<String>> get() = _snackbar_msg

    private val _result_set_area = MutableLiveData<Event<Response<GetModel>>>()
    val result_set_area: LiveData<Event<Response<GetModel>>>
        get() = _result_set_area

    init {
        _cctv_num.postValue(Event(1))
    }

    fun setCctvNum(num: Int) {
        _cctv_num.postValue(Event(num))
    }

    fun setGPS() {
        if (set_latitude.value.isNullOrEmpty()) {
            _snackbar_msg.postValue(Event("위도 입력 요청"))
            return
        }
        if (set_longitude.value.isNullOrEmpty()) {
            _snackbar_msg.postValue(Event("경도 입력 요청"))
            return
        }

        val json_gps = JsonObject()
        json_gps.addProperty("lat", set_latitude.value)
        json_gps.addProperty("lon", set_longitude.value)

        Log.i("!---", json_gps.toString())

        setArea(PostModel(json_gps))
    }

    fun setArea(data: PostModel) {
        viewModelScope.launch {
            val response = repository.setArea(data)
            _result_set_area.postValue(Event(response))
        }
    }

    fun setDraw() {
        var test = JsonObject()

        var test1 = JsonObject()
        test1.addProperty("x", 100)
        test1.addProperty("y", 100)
        var test2 = JsonObject()
        test2.addProperty("x", 200)
        test2.addProperty("y", 100)
        var test3 = JsonObject()
        test3.addProperty("x", 200)
        test3.addProperty("y", 200)
        var test4 = JsonObject()
        test4.addProperty("x", 100)
        test4.addProperty("y", 200)

        test.add("1", test1)
        test.add("2", test2)
        test.add("3", test3)
        test.add("4", test4)

        val ttest = PostDrawModel(test)

        setDraw(ttest)
    }

    fun setDraw(data: PostDrawModel) {
        viewModelScope.launch {
            val response = repository.setDraw(data)
            _result_set_area.postValue(Event(response))
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}