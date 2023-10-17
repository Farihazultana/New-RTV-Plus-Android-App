package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rtvplus.R
import com.rtvplus.data.models.device_info.DeviceInfo
import com.rtvplus.databinding.ActivityMainBinding
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.isOnline
import com.rtvplus.utils.AppUtils.showAlertDialog
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var deviceInfo: DeviceInfo
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        if (!isOnline(this)) {
            showAlertDialog(this)
        }
        setContentView(view)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

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