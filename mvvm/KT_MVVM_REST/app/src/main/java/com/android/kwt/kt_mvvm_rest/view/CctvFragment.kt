package com.android.kwt.kt_mvvm_rest.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.kwt.kt_mvvm_rest.Constants.Companion.BASE_URL
import com.android.kwt.kt_mvvm_rest.databinding.FragmentCctvBinding
import com.android.kwt.kt_mvvm_rest.model.repository.Repository
import com.android.kwt.kt_mvvm_rest.viewmodel.MainViewModel
import com.android.kwt.kt_mvvm_rest.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class CctvFragment : Fragment() {
    private var binding: FragmentCctvBinding? = null

    private val viewModelMain: MainViewModel by activityViewModels { MainViewModelFactory(Repository()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentCctvBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewModelMain
            cctvFragment = this@CctvFragment
        }

        binding?.wvCctv?.webViewClient = WebViewClient()
        binding?.wvCctv?.loadUrl(BASE_URL)

        Log.i("!---2", viewModelMain.cctv_num.value.toString())
        initObserver()
    }

    private fun initObserver() {
        viewModelMain.result_set_area.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                if (it.isSuccessful) {
                    if (it.body()!!.result == "success") {
                        Log.i("!---3", it.body().toString())
                    } else {
                        Log.i("!---4", it.body().toString())
                    }
                }
            }
        }

        viewModelMain.snackbar_msg.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                Snackbar.make(binding?.llayCctv!!, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}