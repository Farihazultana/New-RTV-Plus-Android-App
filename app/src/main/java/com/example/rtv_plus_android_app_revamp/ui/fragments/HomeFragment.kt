package com.example.rtv_plus_android_app_revamp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentHomeBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.SearchActivity
import com.example.rtv_plus_android_app_revamp.ui.adapters.ParentHomeAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.HomeViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentHomeAdapter: ParentHomeAdapter
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.searchIcon.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        })

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val homeRequest = HomeRequest("8801841464604", "home")
        homeViewModel.fetchHomeData("8801841464604", "home")

        parentHomeAdapter = ParentHomeAdapter(emptyList())
        binding.parentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.parentRecyclerview.adapter = parentHomeAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.homeData.collect { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResultType.Success -> {
                        val homeData = result.data
                        parentHomeAdapter.homeData = homeData.data
                        binding.progressBar.visibility = View.GONE
                        binding.parentRecyclerview.visibility = View.VISIBLE
                        parentHomeAdapter.notifyDataSetChanged()
                    }

                    is ResultType.Error -> {
                        val errorMessage = result.exception.message
                        Toast.makeText(requireContext(), "Something is wrong. Please try again", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE

                    }
                }
            }
        }
    }

}