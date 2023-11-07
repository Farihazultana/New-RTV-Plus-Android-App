package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rtvplus.R
import com.rtvplus.data.models.seeAll.Content
import com.rtvplus.databinding.ActivitySeeAllBinding
import com.rtvplus.ui.adapters.SeeAllAdapter
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.ui.viewmodels.SeeAllViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.LogInUtil
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.rtvplus.utils.SocialmediaLoginUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SeeAllActivity : AppCompatActivity(), SeeAllAdapter.itemClickListener,
    LogInUtil.ObserverListener, SocialmediaLoginUtil.ObserverListenerSocial {
    private lateinit var binding: ActivitySeeAllBinding
    private lateinit var seeAllAdapter: SeeAllAdapter
    private val seeAllViewModel by viewModels<SeeAllViewModel>()
    var layoutManager = GridLayoutManager(this, 2)
    private var currentPage = 1
    private var isLoading = false
    private var isLastpage = false
    lateinit var username: String
    private lateinit var signInType: String
    private var isPremiumUser: Int? = 0

    companion object {
        lateinit var catCode: String
        lateinit var catName: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (!AppUtils.isOnline(this)) {
            AppUtils.showAlertDialog(this)
        }

        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolBarBackIconSubscribe.setOnClickListener {
            onBackPressed()
        }

        username = SharedPreferencesUtil.getData(
            this,
            UsernameInputKey,
            ""
        ).toString()

        signInType = SharedPreferencesUtil.getData(this, AppUtils.SignInType, "").toString()
        if (signInType == "Phone") {
            LogInUtil().observeLoginData(this, this, this, this)
        } else {
            SocialmediaLoginUtil().observeSocialLogInData(this, this, this, this)
        }

        isPremiumUser = SharedPreferencesUtil.getSavedLogInData(this)?.play ?: 0

        catCode = intent.getStringExtra("catcode").toString()
        catName = intent.getStringExtra("catname").toString()

        seeAllAdapter = SeeAllAdapter(this, emptyList(), this, isPremiumUser)
        binding.rvSeeAll.layoutManager = layoutManager
        binding.rvSeeAll.adapter = seeAllAdapter


        if (catCode.isNotEmpty()) {
            loadMoreData() // Initial data load

            binding.rvSeeAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastpage) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0
                        ) {
                            isLoading = true
                            currentPage++
                            loadMoreData()
                        }
                    }
                }
            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMoreData() {
        seeAllViewModel.fetchSeeAllData(currentPage.toString(), catCode, "0", "1")

        lifecycleScope.launch {
            seeAllViewModel.seeAllData.observe(this@SeeAllActivity) {
                when (it) {
                    is ResultType.Loading -> {
                        binding.shimmerFrameLayout.startShimmer()
                        binding.shimmerFrameLayout.visibility = View.VISIBLE
                    }

                    is ResultType.Success -> {
                        val seeAllData = it.data

                        if (currentPage == 1) {
                            seeAllAdapter.seeAllData = seeAllData.contents
                            binding.tvSeeAllTitle.text = seeAllData.catname
                        } else {
                            if (!seeAllAdapter.seeAllData?.containsAll(seeAllData.contents)!!) {
                                seeAllAdapter.seeAllData =
                                    seeAllAdapter.seeAllData?.plus(seeAllData.contents)
                            }
                        }

                        binding.shimmerFrameLayout.stopShimmer()
                        binding.shimmerFrameLayout.visibility = View.GONE

                        seeAllAdapter.notifyDataSetChanged()
                        isLoading = false

                        //checking last page
                        isLastpage = seeAllData.contents.isEmpty()
                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            this@SeeAllActivity,
                            R.string.error_response_msg,
                            Toast.LENGTH_SHORT
                        ).show()
                        isLoading = false
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
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

            if (item.contenttype == "playlist") {

                if (username.isNotEmpty()) {

                    if (isPremiumUser.toString() == "0" && item.isfree == "0") {
                        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                        val subscriptionFragment = SubscriptionFragment()
                        fragmentTransaction.replace(
                            R.id.subscriptionContainerView,
                            subscriptionFragment
                        )
                        fragmentTransaction.commit()
                    } else {
                        val intent = Intent(this, PlayerActivity::class.java)
                        intent.putExtra("id", item.contentid)
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            } else {

                if (username.isNotEmpty()) {
                    if (isPremiumUser == 0 && item.isfree == "0") {
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
                            .putExtra("id", item.contentid)
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

    override fun onResume() {
        if (signInType == "Phone") {
            val user =
                SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()
            val password =
                SharedPreferencesUtil.getData(this, AppUtils.UserPasswordKey, "").toString()
            LogInUtil().fetchLogInData(this, user, password)
        } else {
            val user =
                SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()
            val email =
                SharedPreferencesUtil.getData(this, AppUtils.GoogleSignIn_Email, "").toString()
            val firstname =
                SharedPreferencesUtil.getData(this, AppUtils.GoogleSignIn_FirstName, "")
                    .toString()
            val lastname =
                SharedPreferencesUtil.getData(this, AppUtils.GoogleSignIn_LastName, "")
                    .toString()
            val imgUri =
                SharedPreferencesUtil.getData(this, AppUtils.GoogleSignIn_ImgUri, "")
                    .toString()
            Log.i(
                "OneTap",
                "onResume Subscription Fragment: $user, $email, $firstname, $lastname, $imgUri"
            )
            SocialmediaLoginUtil().fetchSocialLogInData(this,"google", user, firstname, lastname, email, imgUri)
        }
        super.onResume()
    }

    override fun observerListener(result: String) {

    }

    override fun observerListenerSocial(result: String, loginSrc: String) {

    }
}