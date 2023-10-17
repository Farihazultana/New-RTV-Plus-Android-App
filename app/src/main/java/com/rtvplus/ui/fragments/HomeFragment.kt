package com.rtvplus.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var deviceInfo: DeviceInfo
    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentHomeAdapter: ParentHomeAdapter
    private val homeViewModel by viewModels<HomeViewModel>()
    private val logInViewModel by viewModels<LogInViewModel>()
    lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        binding.searchIcon.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        binding.tryAgainBtn.setOnClickListener {
            homeViewModel.fetchHomeData("", "home")
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.fetchHomeData("", "home")

        parentHomeAdapter =
            ParentHomeAdapter(requireContext(), emptyList(), findNavController(), null)
        binding.parentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.parentRecyclerview.adapter = parentHomeAdapter

        var backPressedTime: Long = 0

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 3000 > System.currentTimeMillis()) {
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

        username = SharedPreferencesUtil.getData(requireContext(), AppUtils.UsernameInputKey, "").toString()


        if (username.isNotEmpty()) {
            logInViewModel.fetchLogInData(username, "", "yes", "1")
        }
        checkIfPremiumUser()

        displayHomeData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displayHomeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.homeData.collect { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tryAgainBtn.visibility = View.GONE
                    }

                    is ResultType.Success -> {
                        if (checkIfPremiumUser().toString().isNotEmpty()) {
                            val homeData = result.data
                            parentHomeAdapter.homeData = homeData.data
                            parentHomeAdapter.isPemiumUser = checkIfPremiumUser()
                            binding.progressBar.visibility = View.GONE
                            binding.tryAgainBtn.visibility = View.GONE
                            binding.parentRecyclerview.visibility = View.VISIBLE
                            parentHomeAdapter.notifyDataSetChanged()
                        }

                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.error_response_msg,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                        binding.tryAgainBtn.visibility = View.VISIBLE

                    }
                }
            }
        }
    }
    private fun checkIfPremiumUser(): Int {
        var isPremiumUser: Int? = 0
        lifecycleScope.launch(Dispatchers.IO) {
            logInViewModel.logInData.collect {
                when (it) {
                    is ResultType.Success -> {
                        val logInResult = it.data

                        for (item in logInResult) {
                            val result = item.play
                            isPremiumUser = result
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
        }
        return isPremiumUser!!
    }


}