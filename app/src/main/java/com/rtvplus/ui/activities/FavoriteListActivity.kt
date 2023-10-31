package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rtvplus.R
import com.rtvplus.data.models.favorite_list.Content
import com.rtvplus.data.models.favorite_list.FavoriteResponse
import com.rtvplus.data.models.favorite_list.RemoveListResponse
import com.rtvplus.databinding.ActivityFavoriteListBinding
import com.rtvplus.ui.adapters.FavoriteListAdapter
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.ui.viewmodels.FavoriteListViewModel
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.ui.viewmodels.RemoveFavoriteListViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.LogInUtil
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.rtvplus.utils.SocialmediaLoginUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteListActivity : AppCompatActivity(), FavoriteListAdapter.OnRemoveItemClickListener,
    FavoriteListAdapter.itemClickListener,  LogInUtil.ObserverListener, SocialmediaLoginUtil.ObserverListenerSocial {
    lateinit var binding: ActivityFavoriteListBinding
    private val favoriteListViewModel by viewModels<FavoriteListViewModel>()
    private val removeListViewModel by viewModels<RemoveFavoriteListViewModel>()
    private lateinit var favoriteListAdapter: FavoriteListAdapter
    var layoutManager = GridLayoutManager(this, 2)
    private var currentPage = 1
    private var isLoading = false
    private var isLastpage = false
    var isPremiumUser : Int = 0
    private val logInViewModel by viewModels<LogInViewModel>()
    lateinit var username: String
    private lateinit var signInType: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (!AppUtils.isOnline(this)) {
            AppUtils.showAlertDialog(this)
        }

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        isPremiumUser = SharedPreferencesUtil.getSavedLogInData(this)?.play ?: 0

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "My Favourites"
        }
        username = SharedPreferencesUtil.getData(this, UsernameInputKey, "").toString()


        signInType = SharedPreferencesUtil.getData(this, AppUtils.SignInType, "").toString()
        if (signInType == "Phone") {
            LogInUtil().observeLoginData(this, this, this, this)
        } else {
            SocialmediaLoginUtil().observeGoogleLogInData(this, this, this, this)
        }

        if (username.isNotEmpty()) {
            logInViewModel.fetchLogInData(username, "", "yes", "1")
        }

        favoriteListAdapter = FavoriteListAdapter(null, this, this, isPremiumUser)
        binding.favouriteListRecyclerview.layoutManager = GridLayoutManager(this, 2)
        binding.favouriteListRecyclerview.adapter = favoriteListAdapter


        if (username.isNotEmpty()) {
            loadMoreData()

            binding.favouriteListRecyclerview.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
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
                            // End of the list reached, loading more data
                            loadMoreData()
                        }
                    }
                }
            })
        }

        updateDataAfterRemove()

        // getFavoriteContent()
    }

    private fun updateDataAfterRemove() {
        removeListViewModel.removeContentResponse.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                    binding.shimmerFrameLayout.startShimmer()
                }

                is ResultType.Success<*> -> {
                    val response = result.data as RemoveListResponse
                    if (response.status == "success") {
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()
                        Toast.makeText(
                            this@FavoriteListActivity,
                            "remove list ${response.status}",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (username.isNotEmpty()) {
                            favoriteListViewModel.fetchFavoriteContent(username, "1")
                        }
                    } else {
                        Toast.makeText(
                            this@FavoriteListActivity,
                            response.status,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@FavoriteListActivity,
                        R.string.error_response_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun getFavoriteContent() {
        favoriteListViewModel.favoriteContent.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                    binding.shimmerFrameLayout.startShimmer()
                }

                is ResultType.Success<*> -> {
                    val content = result.data as FavoriteResponse
                    if (content.contents.isNotEmpty()) {
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()
                        binding.emptyResultTv.visibility = View.GONE
                        favoriteListAdapter.content = content.contents
                        favoriteListAdapter.isPemiumUser = isPremiumUser
                        favoriteListAdapter.notifyDataSetChanged()
                    } else {
                        favoriteListAdapter.content = emptyList()
                        favoriteListAdapter.notifyDataSetChanged()
                        binding.emptyResultTv.visibility = View.VISIBLE
                        binding.emptyResultTv.text = "No favorite item available"
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@FavoriteListActivity,
                        R.string.error_response_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRemoveItemClicked(contentId: String) {
        val username = SharedPreferencesUtil.getData(this, UsernameInputKey, "").toString()
        if (username.isNotEmpty()) {
            removeListViewModel.removeFavoriteContent(contentId, username)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMoreData() {

        val username = SharedPreferencesUtil.getData(this, UsernameInputKey, "").toString()

        favoriteListViewModel.fetchFavoriteContent(username, currentPage.toString())

        favoriteListViewModel.favoriteContent.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                    binding.shimmerFrameLayout.startShimmer()
                }

                is ResultType.Success<*> -> {
                    val content = result.data as FavoriteResponse
                    if (content.contents.isNotEmpty()) {

                        if (currentPage == 1) {
                            favoriteListAdapter.content = content.contents
                        } else {
                            if (!favoriteListAdapter.content?.containsAll(content.contents)!!) {
                                favoriteListAdapter.content =
                                    favoriteListAdapter.content?.plus(content.contents)
                                favoriteListAdapter.isPemiumUser = isPremiumUser
                            }
                        }
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()
                        binding.emptyResultTv.visibility = View.GONE
                        //  favoriteListAdapter.content = content.contents
                        favoriteListAdapter.notifyDataSetChanged()
                    } else {
                        favoriteListAdapter.content = emptyList()
                        favoriteListAdapter.notifyDataSetChanged()
                        binding.emptyResultTv.visibility = View.VISIBLE
                        binding.emptyResultTv.text = "No favorite item available"
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@FavoriteListActivity,
                        R.string.error_response_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun onItemClickListener(position: Int, item: Content?) {
        val username = SharedPreferencesUtil.getData(this, UsernameInputKey, "")

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
            SocialmediaLoginUtil().fetchGoogleLogInData(
                this,
                user,
                firstname,
                lastname,
                email,
                imgUri
            )
        }

        super.onResume()
    }

    override fun observerListener(result: String) {

    }

    override fun observerListenerSocial(result: String) {

    }

}