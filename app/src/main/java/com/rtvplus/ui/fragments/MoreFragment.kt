package com.rtvplus.ui.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rtvplus.R
import com.rtvplus.databinding.FragmentMoreBinding
import com.rtvplus.ui.activities.FavoriteListActivity
import com.rtvplus.ui.activities.FeedBackActivity
import com.rtvplus.ui.activities.InfoActivity
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.utils.AppUtils.GoogleSignInKey
import com.rtvplus.utils.AppUtils.LogInKey
import com.rtvplus.utils.AppUtils.PACKAGE_NAME
import com.rtvplus.utils.AppUtils.PhoneInputKey
import com.rtvplus.utils.SharedPreferencesUtil

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding

    companion object {
        lateinit var oneTapClient: SignInClient
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.backButton.setOnClickListener {
            val navController = findNavController(binding.root)
            navController.navigate(R.id.HomeFragment)
            val bottomNavigationView =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
            bottomNavigationView.selectedItemId = R.id.HomeFragment
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController(binding.root)
                navController.navigate(R.id.HomeFragment)
                val bottomNavigationView =
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
                bottomNavigationView.selectedItemId = R.id.HomeFragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val googleLoginInfo = SharedPreferencesUtil.getData(
            requireContext(),
            GoogleSignInKey,
            ""
        ).toString()

        val getPhoneNumSP = SharedPreferencesUtil.getData(
            requireContext(),
            PhoneInputKey,
            ""
        )

        binding.favourite.setOnClickListener {
            if (googleLoginInfo.isNotEmpty() || getPhoneNumSP.toString().isNotEmpty()) {
                val intent = Intent(requireContext(), FavoriteListActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }

        }

        binding.help.setOnClickListener {
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("appinfo", "help")
            startActivity(intent)
        }

        binding.privacyPolicy.setOnClickListener {
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("appinfo", "privacy")
            startActivity(intent)
        }
        binding.license.setOnClickListener {
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("appinfo", "license")
            startActivity(intent)
        }
        binding.about.setOnClickListener {
            val intent = Intent(requireContext(), InfoActivity::class.java)
            intent.putExtra("appinfo", "about")
            startActivity(intent)
        }
        binding.feedBack.setOnClickListener {
            val intent = Intent(requireContext(), FeedBackActivity::class.java)
            startActivity(intent)
        }

        binding.rate.setOnClickListener {
            val marketUri = Uri.parse("market://details?id=$PACKAGE_NAME")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            try {
                startActivity(marketIntent)
            } catch (e: ActivityNotFoundException) {
                // If Play Store app is not available, open the app link in a browser
                val webUri =
                    Uri.parse("https://play.google.com/store/apps/details?id=$PACKAGE_NAME")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                startActivity(webIntent)
            }
        }

        if (googleLoginInfo.isNotEmpty()) {
            binding.logInAs.text = "Logged in as: ${googleLoginInfo.toString()}"
            binding.notLoginText.visibility = View.GONE
            binding.logInBtn.visibility = View.GONE
            binding.logout.visibility = View.VISIBLE
            binding.logInAs.visibility = View.VISIBLE
        } else if (getPhoneNumSP.toString().isNotEmpty()) {
            binding.logInAs.text = "Logged in as: ${getPhoneNumSP.toString()}"
            binding.notLoginText.visibility = View.GONE
            binding.logInBtn.visibility = View.GONE
            binding.logout.visibility = View.VISIBLE
        } else {
            binding.notLoginText.visibility = View.VISIBLE
            binding.logInBtn.visibility = View.VISIBLE
            binding.logout.visibility = View.GONE
            binding.logInAs.visibility = View.GONE
        }

        binding.logInBtn.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            if (isOneTapClientInitialized()) {
                SharedPreferencesUtil.removeKey(requireContext(), LogInKey)
                SharedPreferencesUtil.removeKey(requireContext(), GoogleSignInKey)
                SharedPreferencesUtil.clear(requireContext())

                val spResGoogle = SharedPreferencesUtil.getData(
                    requireContext(),
                    GoogleSignInKey,
                    ""
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

//        binding.logout.setOnClickListener {
//            SharedPreferencesUtil.removeKey(requireContext(), LogInKey)
//            SharedPreferencesUtil.removeKey(requireContext(), GoogleSignInKey)
//            binding.logInAs.text = null
//
//            val spResGoogle = SharedPreferencesUtil.getData(
//                requireContext(),
//                GoogleSignInKey,
//                ""
//            )
//            if (spResGoogle.toString().isNotEmpty()) {
//                oneTapClient.signOut().addOnFailureListener {
//                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
//                }.addOnCompleteListener {
//                    Toast.makeText(context, "You are Signed out", Toast.LENGTH_SHORT).show()
//                }
//            }
//            SharedPreferencesUtil.clear(requireContext())
//            Toast.makeText(requireContext(), "You are Logged Out!", Toast.LENGTH_SHORT).show()
//            navigateToHomeFragment()
//        }
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