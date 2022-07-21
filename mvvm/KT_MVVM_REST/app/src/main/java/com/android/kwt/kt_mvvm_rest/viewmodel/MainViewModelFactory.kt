package com.android.kwt.kt_mvvm_rest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.kwt.kt_mvvm_rest.model.repository.Repository

class MainViewModelFactory(
    private val repository : Repository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}