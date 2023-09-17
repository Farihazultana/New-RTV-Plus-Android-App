package com.example.rtv_plus_android_app_revamp.ui.fregments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation.findNavController
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscriptionBinding

class SubscriptionFragment : Fragment(){
    private lateinit var binding: FragmentSubscriptionBinding
    private lateinit var toolBar: Toolbar
    private lateinit var actionBar: ActionBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        //toolBar = binding.toolbar;
        //toolBar.title = "Subscribe"
        val toolBarIconSubscribe = binding.toolBarIconSubscribe
        toolBarIconSubscribe.setOnClickListener {
            val navController = findNavController(binding.root)
            navController.navigate(R.id.HomeFragment)
        }

        return binding.root
    }




}