package com.rtvplus.ui.activities

import android.content.Intent
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
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.ui.viewmodels.SeeAllViewModel
import com.rtvplus.utils.AppUtils.UsernameInputKey
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
    var layoutManager = GridLayoutManager(this, 2)
    private var currentPage = 1
    private var isLoading = false
    private var isLastpage = false
    private var isPremiumUser: Int? = 0
    private val logInViewModel by viewModels<LogInViewModel>()

    companion object {
        lateinit var catCode: String
        lateinit var catName: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolBarBackIconSubscribe.setOnClickListener {
            onBackPressed()
        }

        catCode = intent.getStringExtra("catcode").toString()
        catName = intent.getStringExtra("catname").toString()

        seeAllAdapter = SeeAllAdapter(this, emptyList(), this, null)
        binding.rvSeeAll.layoutManager = layoutManager
        binding.rvSeeAll.adapter = seeAllAdapter


        val username = SharedPreferencesUtil.getData(this, UsernameInputKey, "").toString()

        if (username.isNotEmpty()) {
            logInViewModel.fetchLogInData(username, "", "yes", "1")
        }


        logInViewModel.logInData.observe(this) {
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

    private fun loadMoreData() {
        seeAllViewModel.fetchSeeAllData(currentPage.toString(), catCode, "0", "1")

        lifecycleScope.launch {
            seeAllViewModel.seeAllData.collect {
                when (it) {
                    is ResultType.Loading -> {
                        binding.subscribeProgressBar.visibility = View.VISIBLE
                    }

                    is ResultType.Success -> {
                        val seeAllData = it.data

                        seeAllAdapter.isPemiumUser = isPremiumUser

                        if (currentPage == 1) {
                            seeAllAdapter.seeAllData = seeAllData.contents
                            binding.tvSeeAllTitle.text = seeAllData.catname
                        } else {
                            if (!seeAllAdapter.seeAllData?.containsAll(seeAllData.contents)!!) {
                                seeAllAdapter.seeAllData =
                                    seeAllAdapter.seeAllData?.plus(seeAllData.contents)
                            }
                        }

                        binding.subscribeProgressBar.visibility = View.GONE
                        seeAllAdapter.notifyDataSetChanged()
                        isLoading = false

                        //checking last page
                        isLastpage = seeAllData.contents.isEmpty()
                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            this@SeeAllActivity,
                            "Something is wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        isLoading = false
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

            val username = SharedPreferencesUtil.getData(
                this,
                UsernameInputKey,
                ""
            )
            if (item.contenttype == "playlist") {

                if (username.toString().isNotEmpty()) {

                    if (isPremiumUser.toString() == "0" && item?.isfree == "0") {

                        Log.e("fffffffffffffffffffff", item?.isfree.toString())
                        Log.e("fffffffffffffffffffff", isPremiumUser.toString())

                        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                        val subscriptionFragment = SubscriptionFragment()
                        fragmentTransaction.replace(
                            R.id.subscriptionContainerView,
                            subscriptionFragment
                        )
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    } else {
                        Log.e(
                            "fffffffffffffffffffff",
                            "Inside else block: ${item?.isfree.toString()}"
                        )
                        Log.e(
                            "fffffffffffffffffffff",
                            "Inside else block: ${isPremiumUser.toString()}"
                        )

                        val intent = Intent(this, PlayerActivity::class.java)
                        intent.putExtra("id", item?.contentid)
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            } else {

                if (username.toString().isNotEmpty()) {
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