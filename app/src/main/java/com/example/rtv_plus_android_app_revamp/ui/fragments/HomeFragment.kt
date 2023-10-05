package com.example.rtv_plus_android_app_revamp.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentHomeBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.SearchActivity
import com.example.rtv_plus_android_app_revamp.ui.adapters.ParentHomeAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.HomeViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentHomeAdapter: ParentHomeAdapter
    private var doubleBackPressedOnce = false
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.searchIcon.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        binding.tryAgainBtn.setOnClickListener{
            homeViewModel.fetchHomeData("8801841464604", "home")
        }

        return binding.root
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         homeViewModel.fetchHomeData("8801841464604", "home")

        parentHomeAdapter = ParentHomeAdapter(requireContext(),emptyList())
        binding.parentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.parentRecyclerview.adapter = parentHomeAdapter

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackPressedOnce) {
                    requireActivity().finish()
                } else {
                    doubleBackPressedOnce = true
                    Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleBackPressedOnce = false
                    }, 2000)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.homeData.collect { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tryAgainBtn.visibility = View.GONE
                    }
                    is ResultType.Success -> {
                        val homeData = result.data
                        parentHomeAdapter.homeData = homeData.data
                        binding.progressBar.visibility = View.GONE
                        binding.tryAgainBtn.visibility = View.GONE
                        binding.parentRecyclerview.visibility = View.VISIBLE
                        parentHomeAdapter.notifyDataSetChanged()
                    }

                    is ResultType.Error -> {
                        Toast.makeText(requireContext(), "Something is wrong. Please try again", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.tryAgainBtn.visibility = View.VISIBLE

                    }
                }
            }
        }
    }

}