package com.example.rtv_plus_android_app_revamp.ui.fregments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentHomeBinding
import com.example.rtv_plus_android_app_revamp.databinding.FragmentLiveTvBinding

class LiveTvFragment : Fragment() {
    private lateinit var binding: FragmentLiveTvBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLiveTvBinding.inflate(inflater, container, false)
        return binding.root
    }

}