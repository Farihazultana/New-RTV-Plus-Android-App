package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rtvplus.R
import com.rtvplus.data.models.device_info.DeviceInfo
import com.rtvplus.databinding.ActivityMainBinding
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.isOnline
import com.rtvplus.utils.AppUtils.showAlertDialog
import com.rtvplus.utils.LogInUtil
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.rtvplus.utils.SocialmediaLoginUtil
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
    var currentversion: Int? = 0
    var enforcetext: String? = null
    private var enforce: Int? = 0
    var  username : String ? = ""
    private val logInViewModel by viewModels<LogInViewModel>()
    private lateinit var signInType: String

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        if (!isOnline(this)) {
            showAlertDialog(this)
        }
        setContentView(view)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)

         username = SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()



        binding.bottomNavigationBarId.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.LiveTvFragment && username!!.isEmpty()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                selectedItemId = R.id.HomeFragment
            } else {
                if (item.itemId != binding.bottomNavigationBarId.selectedItemId)
                    NavigationUI.onNavDestinationSelected(item, navController)

            }
            true
        }


        //  setupWithNavController(bottomNavigationView, navController)

//                binding.bottomNavigationBarId.setOnItemSelectedListener { menuItem ->
//            val itemId = menuItem.itemId
//            if (selectedItemId != itemId) {
//                when (itemId) {
//                    R.id.HomeFragment -> navController.navigate(R.id.HomeFragment)
//                    R.id.LiveTvFragment -> {
//                        if (username.isNotEmpty()) {
//                            navController.navigate(R.id.LiveTvFragment)
//                        } else {
//                            val intent = Intent(this, LoginActivity::class.java)
//                            startActivity(intent)
//                            selectedItemId = R.id.HomeFragment
//                        }
//                    }
//
//                    R.id.SubscriptionFragment -> navController.navigate(R.id.SubscriptionFragment)
//                    R.id.MoreFragment -> navController.navigate(R.id.MoreFragment)
//                }
//                selectedItemId = itemId
//            }
//            true
//        }


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
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish()
        } else {
            Toast.makeText(
                this,
                "Press back again to leave the app.",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun checkSoftwareVersion() {

        Log.e("softwareVersion", deviceInfo.versionCode.toString())
        Log.e("softwareVersion", currentversion.toString())

        if (deviceInfo.versionCode < currentversion!!.toInt()) {

            displayVersionUpdateScreen()

        }
    }

    private fun displayVersionUpdateScreen() {
        Log.e("softwareVersion", "Display")
        val inflater = LayoutInflater.from(this)
        val customDialogView =
            inflater.inflate(R.layout.custom_alert_dialog_version_check, null)

        val alertDescription =
            customDialogView.findViewById<TextView>(R.id.alertDescriptionText)
        val updateNowButton =
            customDialogView.findViewById<Button>(R.id.updateNow)
        val notNowButton =
            customDialogView.findViewById<Button>(R.id.notNow)

        alertDescription.text = enforcetext

        // Create the AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(customDialogView)
        val alertDialog = alertDialogBuilder.create()

        if (enforce == 1) {
            notNowButton.visibility = View.GONE
            alertDialog.setCancelable(false)
        }

        // Set a click listener for the Confirm button
        updateNowButton.setOnClickListener {
            goToPlayStore()
        }

        notNowButton.setOnClickListener {
            alertDialog.dismiss()
        }


        alertDialog.show()
    }

    private fun goToPlayStore() {
        val marketUri = Uri.parse("market://details?id=${AppUtils.PACKAGE_NAME}")
        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
        try {
            startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            // If Play Store app is not available, open the app link in a browser
            val webUri =
                Uri.parse("https://play.google.com/store/apps/details?id=${AppUtils.PACKAGE_NAME}")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(webIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        username = SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()
    }


}