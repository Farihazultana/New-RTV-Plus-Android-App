package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.rtvplus.R
import com.rtvplus.databinding.ActivityMainBinding
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.isOnline
import com.rtvplus.utils.AppUtils.showAlertDialog
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var selectedItemId: Int = -1
    lateinit var navHostFragment : NavHostFragment

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

        val googleLoginInfo = SharedPreferencesUtil.getData(
            this,
            AppUtils.GoogleSignInKey,
            ""
        ).toString()

        val phoneLoginInfo = SharedPreferencesUtil.getData(
            this,
            AppUtils.PhoneInputKey,
            ""
        )

        val fragmentTag = intent.getStringExtra("fragmentToShow")
        if (fragmentTag == "YourFragment") {
            val fragment = SubscriptionFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, fragmentTag)
                .commit()
            selectedItemId = R.id.SubscriptionFragment
        }

        binding.bottomNavigationBarId.setOnItemSelectedListener { menuItem ->
            val itemId = menuItem.itemId
            if (selectedItemId != itemId) {
                when (itemId) {
                    R.id.HomeFragment -> navController.navigate(R.id.HomeFragment)
                    R.id.LiveTvFragment -> {
                        if (googleLoginInfo.isNotEmpty() || phoneLoginInfo.toString()
                                .isNotEmpty()
                        ) {
                            navController.navigate(R.id.LiveTvFragment)
                        } else {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            selectedItemId = R.id.HomeFragment
                        }
                    }

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