package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavigationBarId.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.HomeFragment -> navController.navigate(R.id.HomeFragment)
                R.id.LiveTvFragment -> navController.navigate(R.id.LiveTvFragment)
                R.id.SubscriptionFragment -> navController.navigate(R.id.SubscriptionFragment)
                R.id.MoreFragment -> navController.navigate(R.id.MoreFragment)
            }
            true
        }
        binding.bottomNavigationBarId.setItemIconTintList(
            ContextCompat.getColorStateList(
                this,
                R.drawable.selected_nav_item_color
            )
        );
    }
}