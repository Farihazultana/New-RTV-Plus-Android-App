package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rtvplus.R
import com.rtvplus.data.models.device_info.DeviceInfo
import com.rtvplus.databinding.ActivityMainBinding
import com.rtvplus.ui.fragments.HomeFragment
import com.rtvplus.ui.fragments.LiveTvFragment
import com.rtvplus.ui.fragments.MoreFragment
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
    var selectedItemId: Int = -1
    lateinit var navHostFragment: NavHostFragment
    var backPressedTime: Long = 0
    var doubleBackToExitPressedOnce = false

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }

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

        var bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)

        binding.bottomNavigationBarId.setupWithNavController(navController)

        val sub = intent.getStringExtra("subscription")
        if (sub == "subscription"){
            navController.navigate(R.id.SubscriptionFragment)
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
            bottomNavigationView.selectedItemId = R.id.SubscriptionFragment
        }


        val username = SharedPreferencesUtil.getData(
            this,
            AppUtils.UsernameInputKey,
            ""
        ).toString()

       // setupWithNavController(bottomNavigationView, navController)




        // Add HomeFragment initially to the back stack
//        if (supportFragmentManager.backStackEntryCount == 0) {
//            val homeFragment = HomeFragment()
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragmentContainerView, homeFragment, R.id.HomeFragment.toString())
//            transaction.addToBackStack(R.id.HomeFragment.toString())
//            transaction.commit()
//            selectedItemId = R.id.HomeFragment
//            binding.bottomNavigationBarId.selectedItemId = selectedItemId
//        }


        // Inside your activity's onCreate or where you set up your BottomNavigationView
        binding.bottomNavigationBarId.setOnItemSelectedListener { menuItem ->
            val itemId = menuItem.itemId
            if (selectedItemId != itemId) {
                val transaction = supportFragmentManager.beginTransaction()
                val fragmentTag = itemId.toString()
                var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)

                if (fragment == null) {
                    // Fragment not in back stack, create a new instance
                    fragment = when (itemId) {
                        R.id.HomeFragment -> HomeFragment()
                        R.id.LiveTvFragment -> LiveTvFragment()
                        R.id.SubscriptionFragment -> SubscriptionFragment()
                        R.id.MoreFragment -> MoreFragment()
                        else -> HomeFragment() // Handle default case
                    }
                    transaction.add(R.id.fragmentContainerView, fragment, fragmentTag)
                } else {
                    // Fragment is already in back stack, reattach it
                    transaction.attach(fragment)
                }

                // Hide previously attached fragment
                val currentFragment = supportFragmentManager.findFragmentByTag(selectedItemId.toString())
                if (currentFragment != null) {
                    transaction.detach(currentFragment)
                }

                // Add the transaction to the back stack
                transaction.addToBackStack(fragmentTag)

                // Commit the transaction
                transaction.commit()

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


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    fun showBottomNavigationBar() {
        binding.bottomNavigationBarId.visibility = View.VISIBLE
    }

    fun hideBottomNavigationBar() {
        binding.bottomNavigationBarId.visibility = View.GONE
    }

    private var backPressedOnce = false


    private var doubleBackPressedOnce = false

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        if (currentFragment is HomeFragment) {
            // If the current fragment is HomeFragment
            if (backPressedTime + 3000 > System.currentTimeMillis()) {
                super.onBackPressed()
                finish()
            } else {
                Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show()
            }
            backPressedTime = System.currentTimeMillis()
        } else {
            // If the current fragment is not HomeFragment
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Pop the back stack
                supportFragmentManager.popBackStackImmediate()
                // Update selected item in BottomNavigationView
                val backStackEntryCount = supportFragmentManager.backStackEntryCount
                val previousFragmentTag = if (backStackEntryCount > 0) {
                    supportFragmentManager.getBackStackEntryAt(backStackEntryCount - 1).name
                } else {
                    // If no more fragments in the back stack, navigate to HomeFragment
                    navigateToHomeFragment()
                    return
                }
                selectedItemId = previousFragmentTag?.toIntOrNull() ?: R.id.HomeFragment
                binding.bottomNavigationBarId.selectedItemId = selectedItemId
            }
        }
    }


    private fun navigateToHomeFragment() {
        val homeFragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, homeFragment, R.id.HomeFragment.toString())
        transaction.addToBackStack(R.id.HomeFragment.toString())
        transaction.commit()
        selectedItemId = R.id.HomeFragment
        binding.bottomNavigationBarId.selectedItemId = selectedItemId
    }





}