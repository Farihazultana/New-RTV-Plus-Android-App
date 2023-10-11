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
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding

    companion object {
        lateinit var oneTapClient: SignInClient
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        val userPhone = SharedPreferencesUtil.getData(requireContext(), LoginActivity.UsernameInputKey, "").toString()
        val userGoogle = SharedPreferencesUtil.getData(requireContext(), LoginActivity.UsernameInputKey, "").toString()
        if(userPhone.isNotEmpty()){
            binding.tvUserName.text = userPhone
        }
        else if (userGoogle.isNotEmpty()){
            binding.tvUserName.text = userGoogle
        }


        binding.btnLogout.setOnClickListener {
            SharedPreferencesUtil.clear(requireContext())
            if (isOneTapClientInitialized()) {
                SharedPreferencesUtil.clear(requireContext())
                binding.tvUserName.text = null

                val spResGoogle = SharedPreferencesUtil.getData(
                    requireContext(),
                    LoginActivity.GoogleSignInKey,
                    "default_value"
                )
                if (spResGoogle.toString().isNotEmpty()) {
                    LoginActivity.showOneTapUI = false
                    oneTapClient.signOut().addOnFailureListener {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }.addOnCompleteListener {
                        Toast.makeText(context, "You are Signed out", Toast.LENGTH_SHORT).show()
                    }
                }
                Toast.makeText(requireContext(), "You are Logged Out!", Toast.LENGTH_SHORT).show()
                navigateToHomeFragment()
            } else {
                Toast.makeText(
                    requireContext(),
                    "OneTapClient is not initialized",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return binding.root
    }

    private fun navigateToHomeFragment() {
        val navController = Navigation.findNavController(binding.root)
        navController.navigate(R.id.HomeFragment)
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
        bottomNavigationView.selectedItemId = R.id.HomeFragment
    }

    private fun isOneTapClientInitialized(): Boolean {
        return try {
            //LoginActivity.oneTapClient != null
            oneTapClient = Identity.getSignInClient(requireActivity())
            return oneTapClient != null
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }
}