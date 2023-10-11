package com.rtvplus.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.rtvplus.R
import com.rtvplus.data.models.seeAll.Content
import com.rtvplus.databinding.ActivitySeeAllBinding
import com.rtvplus.ui.adapters.SeeAllAdapter
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.ui.viewmodels.SeeAllViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SeeAllActivity : AppCompatActivity(), SeeAllAdapter.itemClickListener {

    private lateinit var binding: ActivitySeeAllBinding
    private lateinit var seeAllAdapter: SeeAllAdapter
    private val seeAllViewModel by viewModels<SeeAllViewModel>()
    private val logInViewModel by viewModels<LogInViewModel>()
    private var isPremiumUser: Int? = null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolBarBackIconSubscribe.setOnClickListener {
            onBackPressed()
        }

        val catCode = intent.getStringExtra("catcode")
        val catName = intent.getStringExtra("catname")

        seeAllViewModel.fetchSeeAllData("1", catCode.toString(), "0", "1")


        seeAllAdapter = SeeAllAdapter(this, emptyList(), this, null)
        binding.rvSeeAll.layoutManager = GridLayoutManager(this, 2)
        binding.rvSeeAll.adapter = seeAllAdapter


        val userEmail = SharedPreferencesUtil.getData(this, AppUtils.GoogleSignInKey, "").toString()
        val userPhone = SharedPreferencesUtil.getData(this, AppUtils.PhoneInputKey, "").toString()

        if (userEmail.isNotEmpty()) {
            logInViewModel.fetchLogInData(userEmail, "", "yes", "1")
        } else if (userPhone.isNotEmpty()) {
            logInViewModel.fetchLogInData(userPhone, "", "yes", "1")
        }

        lifecycleScope.launch(Dispatchers.IO) {
            logInViewModel.logInData.collect {
                when (it) {
                    is ResultType.Success -> {
                        val logInResult = it.data

                        for (item in logInResult) {
                            val result = item.play
                            isPremiumUser = result
                        }
                    }

                    is ResultType.Error -> {

                    }

                    else -> {

                    }
                }
            }

        }

        if (catCode != null) {
            lifecycleScope.launch {
                seeAllViewModel.seeAllData.collect {
                    when (it) {
                        is ResultType.Loading -> {
                            binding.subscribeProgressBar.visibility = View.VISIBLE
                        }

                        is ResultType.Success -> {
                            val seeAllData = it.data
                            if (isPremiumUser.toString().isNotEmpty()) {
                                seeAllAdapter.seeAllData = seeAllData.contents
                                seeAllAdapter.isPemiumUser = isPremiumUser
                                binding.tvSeeAllTitle.text = catName
                                binding.subscribeProgressBar.visibility = View.GONE
                                seeAllAdapter.notifyDataSetChanged()
                            }
                        }

                        is ResultType.Error -> {
                            Toast.makeText(
                                this@SeeAllActivity,
                                "Something is wrong. Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val backStackEntryCount = fragmentManager.backStackEntryCount

        if (backStackEntryCount > 0) {
            // Pop the fragment on the first back button click
            fragmentManager.popBackStack()
        } else {
            // If the back stack is empty, navigate back or exit the activity
            super.onBackPressed()
        }
    }
    override fun onItemClickListener(position: Int, item: Content?) {

        if (item != null) {

            val phone = SharedPreferencesUtil.getData(
                this,
                AppUtils.LogInKey,
                ""
            )
            val email = SharedPreferencesUtil.getData(
                this,
                AppUtils.GoogleSignInKey,
                ""
            )

            Log.e("fffffffffffffffffffff", phone.toString())
            Log.e("fffffffffffffffffffff", email.toString())

            if (item.contenttype == "playlist") {

                if (phone.toString().isNotEmpty() || email.toString().isNotEmpty()) {
                    if (isPremiumUser.toString() == "0" && item?.isfree == "0") {
                        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                        val subscriptionFragment = SubscriptionFragment()
                        fragmentTransaction.replace(
                            R.id.subscriptionContainerView,
                            subscriptionFragment
                        )
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    } else {
                        val intent = Intent(this, PlayerActivity::class.java)
                        intent.putExtra("id", item?.contentid)
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            } else {

                if (phone.toString().isNotEmpty() || email.toString().isNotEmpty()) {
                    if (isPremiumUser == 0 && item?.isfree == "0") {

                        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                        val subscriptionFragment = SubscriptionFragment()
                        fragmentTransaction.replace(
                            R.id.subscriptionContainerView,
                            subscriptionFragment
                        )
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()

                    } else {
                        val intent = Intent(this, PlayerActivity::class.java)
                            .putExtra("id", item?.contentid)
                            .putExtra("type", "single")
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            }


        }
    }
}