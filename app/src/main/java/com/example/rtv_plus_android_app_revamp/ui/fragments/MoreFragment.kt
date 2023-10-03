package com.example.rtv_plus_android_app_revamp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentMoreBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.FavoriteListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
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

        return binding.root
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                requireActivity().onBackPressed()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
}

