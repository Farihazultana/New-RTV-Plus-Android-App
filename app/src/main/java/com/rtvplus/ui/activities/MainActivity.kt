package com.rtvplus.ui.activities

import android.annotation.SuppressLint
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

        val sub = intent.getStringExtra("subscription")
        if (sub == "subscription"){
            navController.navigate(R.id.SubscriptionFragment)
           //bottomNavigationView.selectedItemId = R.id.SubscriptionFragment
        }


        val username = SharedPreferencesUtil.getData(
            this,
            AppUtils.UsernameInputKey,
            ""
        ).toString()


      //  val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
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

    override fun onBackPressed() {

        super.onBackPressed()

//        if (System.currentTimeMillis() - backPressedTime < DOUBLE_BACK_PRESS_INTERVAL) {
//            super.onBackPressed()
//            finish()
//        } else {
//            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
//        }
//        backPressedTime = System.currentTimeMillis()
    }


}