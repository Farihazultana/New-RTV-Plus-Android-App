package com.example.rtv_plus_android_app_revamp.ui.fregments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSearchBinding
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscriptionBinding

class SubscriptionFragment : Fragment() {
    private lateinit var binding: FragmentSubscriptionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }


}