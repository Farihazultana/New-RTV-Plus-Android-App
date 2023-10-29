package com.rtvplus.ui.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rtvplus.R
import com.rtvplus.data.models.device_info.DeviceInfo
import com.rtvplus.databinding.FragmentHomeBinding
import com.rtvplus.ui.activities.MainActivity
import com.rtvplus.ui.activities.SearchActivity
import com.rtvplus.ui.adapters.ParentHomeAdapter
import com.rtvplus.ui.viewmodels.HomeViewModel
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var deviceInfo: DeviceInfo

    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentHomeAdapter: ParentHomeAdapter
    private var doubleBackPressedOnce = false
    private val homeViewModel by viewModels<HomeViewModel>()
    private val logInViewModel by viewModels<LogInViewModel>()
    private var isPremiumUser: Int? = 0
    var username: String? = ""
    var currentversion: Int? = 0
    var enforcetext: String? = null
    private var enforce: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        username =
            SharedPreferencesUtil.getData(requireContext(), AppUtils.UsernameInputKey, "")
                .toString()

        binding.searchIcon.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        binding.tryAgainBtn.setOnClickListener {
            homeViewModel.fetchHomeData(username!!, "home", "3", "app", "en")
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val deviceId = deviceInfo.deviceId
        val softwareVersion = deviceInfo.softwareVersion
        val brand = deviceInfo.brand
        val model = deviceInfo.model
        val release = deviceInfo.release
        val sdkVersion = deviceInfo.sdkVersion
        val versionCode = deviceInfo.versionCode
        val simSerialNumber = deviceInfo.simSerialNumber
        val simOperatorName = deviceInfo.operatorName


        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.fetchHomeData(username!!, "home", "3", "app", "en")

        parentHomeAdapter =
            ParentHomeAdapter(requireContext(), emptyList(), null, lifecycle, this)
        binding.parentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.parentRecyclerview.adapter = parentHomeAdapter

        var backPressedTime: Long = 0

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Press back again to leave the app.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        if (username!!.isNotEmpty()) {
            logInViewModel.fetchLogInData(username!!, "", "yes", "1")
        }

        logInViewModel.logInData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultType.Success -> {
                    val logInResult = it.data

                    for (item in logInResult) {
                        currentversion = item.currentversion
                        enforcetext = item.enforcetext
                        enforce = item.enforce
                        isPremiumUser = item.play

                        checkSoftwareVersion()
                    }
                }

                is ResultType.Error -> {
                    isPremiumUser = 0
                }

                else -> {
                    isPremiumUser = 0
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.homeData.collect { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.tryAgainBtn.visibility = View.GONE
                        binding.shimmerFrameLayout.startShimmer()
                        binding.shimmerFrameLayout.visibility = View.VISIBLE
                    }

                    is ResultType.Success -> {
                        if (isPremiumUser.toString().isNotEmpty()) {
                            val homeData = result.data
                            parentHomeAdapter.homeData = homeData.data
                            parentHomeAdapter.isPemiumUser = isPremiumUser
                            binding.tryAgainBtn.visibility = View.GONE
                            binding.parentRecyclerview.visibility = View.VISIBLE
                            parentHomeAdapter.notifyDataSetChanged()
                            binding.shimmerFrameLayout.stopShimmer()
                            binding.shimmerFrameLayout.visibility = View.GONE

                        }

                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Something is wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.tryAgainBtn.visibility = View.VISIBLE

                    }
                }
            }
        }
    }

    // Function to bind a fragment to the FragmentContainerView
    fun bindFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(android.R.id.content, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.fetchHomeData(username!!, "home", "3", "app", "en")
    }

    private fun checkSoftwareVersion() {

        Log.e("softwareVersion", deviceInfo.versionCode.toString())
        Log.e("softwareVersion", currentversion.toString())

        if (deviceInfo.versionCode < currentversion!!.toInt()) {

            displayVersionUpdateScreen()

        }
    }

    private fun displayVersionUpdateScreen() {
        Log.e("softwareVersion", "Display")
        val inflater = LayoutInflater.from(requireActivity())
        val customDialogView =
            inflater.inflate(R.layout.custom_alert_dialog_version_check, null)


        val alertDescription =
            customDialogView.findViewById<TextView>(R.id.alertDescriptionText)
        val updateNowButton =
            customDialogView.findViewById<Button>(R.id.updateNow)
        val notNowButton =
            customDialogView.findViewById<Button>(R.id.notNow)

        alertDescription.text = enforcetext

        // Create the AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        alertDialogBuilder.setView(customDialogView)
        val alertDialog = alertDialogBuilder.create()

        if (enforce == 1) {
            notNowButton.visibility = View.GONE
            alertDialog.setCancelable(false)
        }

        // Set a click listener for the Confirm button
        updateNowButton.setOnClickListener {
            goToPlayStore()
        }

        notNowButton.setOnClickListener {
            alertDialog.dismiss()
        }


        alertDialog.show()
    }

    private fun goToPlayStore() {
        val marketUri = Uri.parse("market://details?id=${AppUtils.PACKAGE_NAME}")
        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
        try {
            startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            // If Play Store app is not available, open the app link in a browser
            val webUri =
                Uri.parse("https://play.google.com/store/apps/details?id=${AppUtils.PACKAGE_NAME}")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(webIntent)
        }
    }


}