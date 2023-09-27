package com.example.rtv_plus_android_app_revamp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentMoreBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.LoginActivity
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil
import com.google.android.material.bottomnavigation.BottomNavigationView

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)

        binding.btnLogout.setOnClickListener {
            SharedPreferencesUtil.removeKey(requireContext(), LoginActivity.LogInKey)
            Toast.makeText(requireContext(), "You are Logged Out!", Toast.LENGTH_SHORT).show()
            navigateToHomeFragment()
        }
        return binding.root
    }

    private fun navigateToHomeFragment() {
        val navController = Navigation.findNavController(binding.root)
        navController.navigate(R.id.HomeFragment)
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
        bottomNavigationView.selectedItemId = R.id.HomeFragment
    }
}