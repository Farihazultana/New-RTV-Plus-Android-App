package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.ActivityMainBinding
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
        var navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        var navController = navHostFragment.findNavController()

        val destinationFragment = intent.getStringExtra("destinationFragment")
        if (destinationFragment == "subscriptionFragment") {
            // Navigate to the SubscriptionFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.SubscriptionFragment) as NavHostFragment
             val navController = navHostFragment.navController
            navController.navigate(R.id.SubscriptionFragment)
        }

        var selectedItemId: Int = -1

        binding.bottomNavigationBarId.setOnItemSelectedListener { menuItem ->
            val itemId = menuItem.itemId
            if (selectedItemId != itemId) {
                when (itemId) {
                    R.id.HomeFragment -> navController.navigate(R.id.HomeFragment)
                    R.id.LiveTvFragment -> navController.navigate(R.id.LiveTvFragment)
                    R.id.SubscriptionFragment -> navController.navigate(R.id.SubscriptionFragment)
                    R.id.MoreFragment -> navController.navigate(R.id.MoreFragment)
                }
                selectedItemId = itemId
            }
            true
        }

        binding.bottomNavigationBarId.setItemIconTintList(
            ContextCompat.getColorStateList(
                this,
                R.drawable.selected_nav_item_color
            )
        )
    }

    fun showBottomNavigationBar() {
        binding.bottomNavigationBarId.visibility = View.VISIBLE
    }

    fun hideBottomNavigationBar() {
        binding.bottomNavigationBarId.visibility = View.GONE
    }
}