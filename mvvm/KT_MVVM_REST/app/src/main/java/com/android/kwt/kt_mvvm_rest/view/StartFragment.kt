package com.android.kwt.kt_mvvm_rest.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.kwt.kt_mvvm_rest.R
import com.android.kwt.kt_mvvm_rest.databinding.FragmentStartBinding
import com.android.kwt.kt_mvvm_rest.model.repository.Repository
import com.android.kwt.kt_mvvm_rest.viewmodel.MainViewModel
import com.android.kwt.kt_mvvm_rest.viewmodel.MainViewModelFactory

class StartFragment : Fragment() {
    private var binding: FragmentStartBinding? = null

    private val viewModelMain: MainViewModel by activityViewModels { MainViewModelFactory(Repository()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewModelMain
            startFragment = this@StartFragment
        }
    }

    fun connectCctv(num: Int) {
        viewModelMain.setCctvNum(num)

        Log.i("!---1", viewModelMain.cctv_num.value.toString())
        findNavController().navigate(R.id.action_startFragment_to_cctvFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}