package com.rtvplus.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rtvplus.R
import com.rtvplus.databinding.FragmentMoreBinding
import com.rtvplus.ui.activities.FavoriteListActivity
import com.rtvplus.ui.activities.FeedBackActivity
import com.rtvplus.ui.activities.InfoActivity
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.ui.activities.MainActivity
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.PACKAGE_NAME
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.SharedPreferencesUtil

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    private lateinit var dialog: Dialog
    private lateinit var oneTapClient: SignInClient

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        binding.backButton.setOnClickListener {
            val navController = findNavController(binding.root)
            navController.navigate(R.id.HomeFragment)
            val bottomNavigationView =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
            bottomNavigationView.selectedItemId = R.id.HomeFragment
        }
//        val fragmentManager = requireActivity().supportFragmentManager
//
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                fragmentManager.popBackStack()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        var username = SharedPreferencesUtil.getData(
            requireContext(),
            UsernameInputKey,
            ""
        ).toString()

        //To check if signed in with google
        val signInType = SharedPreferencesUtil.getData(requireActivity(), AppUtils.SignInType, "")
        val email = SharedPreferencesUtil.getData(requireContext(), AppUtils.GoogleSignIn_Email, "").toString()
        if (signInType == "Google") {
            username = email
            binding.imgSocialLoginProfile.visibility = View.VISIBLE
            val imgUri =
                SharedPreferencesUtil.getData(requireContext(), AppUtils.GoogleSignIn_ImgUri, "")
                    .toString()
            Glide.with(requireActivity()).load(imgUri)
                .placeholder(R.drawable.no_img)
                .fitCenter().transform(RoundedCorners(50))
                .error(R.drawable.no_img)
                .into(binding.imgSocialLoginProfile)
        }

        binding.favourite.setOnClickListener {
            if (username.isNotEmpty()) {
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
            if (username.isNotEmpty()) {
                val intent = Intent(requireContext(), FeedBackActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
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

        if (username.isNotEmpty()) {
            if (username == email) {
                val gmailUser = SharedPreferencesUtil.getData(requireContext(),AppUtils.GoogleSignIn_dpName, "").toString()
                binding.logInAs.text = gmailUser
            } else {

                binding.logInAs.text = "Logged in as: ${username.substring(2)}"
            }
            binding.notLoginText.visibility = View.GONE
            binding.logInBtn.visibility = View.GONE
            binding.logout.visibility = View.VISIBLE
            binding.logInAs.visibility = View.VISIBLE
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

        //Logout
        setDialog()
        binding.logout.setOnClickListener {
            handleLogoutClick(username)
        }
        return binding.root
    }

    private fun handleLogoutClick(username: String) {

        val btnLogout = dialog.findViewById<Button>(R.id.btnLogout)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        dialog.show()
        btnLogout.setOnClickListener {
            logout(username)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    private fun logout(username: String) {
        SharedPreferencesUtil.clear(requireContext())
        Toast.makeText(context, "You are Logged Out!", Toast.LENGTH_SHORT).show()
        if (isOneTapClientInitialized()) {
            SharedPreferencesUtil.clear(requireContext())
            binding.logInAs.text = null

            if (username.isNotEmpty()) {
                oneTapClient.signOut().addOnFailureListener {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }.addOnCompleteListener {
                    //Toast.makeText(context, "You are Signed Out!", Toast.LENGTH_SHORT).show()
                }
            }

            navigateToHomeFragment()
        } else {
            Toast.makeText(
                requireContext(),
                "OneTapClient is not initialized",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.logout_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(true)
        dialog.window!!.attributes!!.windowAnimations = R.style.animation
    }

    private fun isOneTapClientInitialized(): Boolean {
        return try {
            oneTapClient = Identity.getSignInClient(requireActivity())
            return true
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }

    private fun navigateToHomeFragment() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}