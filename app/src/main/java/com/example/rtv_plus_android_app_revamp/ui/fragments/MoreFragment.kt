package com.example.rtv_plus_android_app_revamp.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentMoreBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.LoginActivity
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.Navigation.findNavController
import com.example.rtv_plus_android_app_revamp.ui.activities.FavoriteListActivity
import com.example.rtv_plus_android_app_revamp.ui.activities.InfoActivity
import com.example.rtv_plus_android_app_revamp.ui.activities.PlayerActivity
import com.example.rtv_plus_android_app_revamp.utils.AppUtils.PACKAGE_NAME

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    companion object{
        lateinit var oneTapClient: SignInClient
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val toolbar = binding.toolbar

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController(binding.root)
                navController.navigate(R.id.HomeFragment)
                val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
                bottomNavigationView.selectedItemId = R.id.HomeFragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "More"
        }

        binding.favourite.setOnClickListener {
            val intent = Intent(requireContext(), FavoriteListActivity::class.java)
            startActivity(intent)
        }

        binding.help.setOnClickListener{
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("appinfo", "help")
            startActivity(intent)
        }

        binding.privacyPolicy.setOnClickListener{
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("appinfo", "privacy")
            startActivity(intent)
        }
        binding.license.setOnClickListener{
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("appinfo", "license")
            startActivity(intent)
        }
        binding.about.setOnClickListener{
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("appinfo", "about")
            startActivity(intent)
        }

        binding.rate.setOnClickListener {
            val marketUri = Uri.parse("market://details?id=$PACKAGE_NAME")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            try {
                startActivity(marketIntent)
            } catch (e: ActivityNotFoundException) {
                // If Play Store app is not available, open the app link in a browser
                val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$PACKAGE_NAME")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                startActivity(webIntent)
            }
        }



        binding.logInAs.text =
            SharedPreferencesUtil.getData(requireContext(),LoginActivity.GoogleSignInKey,"default_value")
                .toString()

        binding.logout.setOnClickListener {
            if (isOneTapClientInitialized()){
                SharedPreferencesUtil.removeKey(requireContext(), LoginActivity.LogInKey)
                SharedPreferencesUtil.removeKey(requireContext(), LoginActivity.GoogleSignInKey)
                binding.logInAs.text = null

                val spResGoogle = SharedPreferencesUtil.getData(requireContext(), LoginActivity.GoogleSignInKey, "default_value")
                if (spResGoogle.toString().isNotEmpty()){
                    LoginActivity.showOneTapUI=false
                    oneTapClient.signOut().addOnFailureListener{
                        Toast.makeText(context,"Something went wrong", Toast.LENGTH_SHORT).show()
                    }.addOnCompleteListener {
                        Toast.makeText(context,"You are Signed out", Toast.LENGTH_SHORT).show()
                    }
                }
                Toast.makeText(requireContext(), "You are Logged Out!", Toast.LENGTH_SHORT).show()
                navigateToHomeFragment()
            }else {
                Toast.makeText(requireContext(), "OneTapClient is not initialized", Toast.LENGTH_SHORT).show()
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
            oneTapClient=Identity.getSignInClient(requireActivity())
            return oneTapClient != null
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }
}