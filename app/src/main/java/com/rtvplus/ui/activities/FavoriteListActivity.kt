package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteListActivity : AppCompatActivity(), FavoriteListAdapter.OnRemoveItemClickListener,
    FavoriteListAdapter.itemClickListener {
    lateinit var binding: ActivityFavoriteListBinding
    private val favoriteListViewModel by viewModels<FavoriteListViewModel>()
    private val removeListViewModel by viewModels<RemoveFavoriteListViewModel>()
    private lateinit var favoriteListAdapter: FavoriteListAdapter
    var layoutManager = GridLayoutManager(this, 2)
    private var currentPage = 1
    private var isLoading = false
    private var isLastpage = false
    private val logInViewModel by viewModels<LogInViewModel>()
    private var isPremiumUser: Int? = 0

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "My Favourites"
        }

        val username = SharedPreferencesUtil.getData(this, UsernameInputKey, "").toString()


        if (username.isNotEmpty()) {
            logInViewModel.fetchLogInData(username, "", "yes", "1")
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
                        isPremiumUser = 0

                    }

                    else -> {

                    }
                }
            }

        }
        favoriteListAdapter = FavoriteListAdapter(null, this, this, isPremiumUser)
        binding.favouriteListRecyclerview.layoutManager = GridLayoutManager(this, 2)
        binding.favouriteListRecyclerview.adapter = favoriteListAdapter



        if (username.toString().isNotEmpty()) {
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

        removeListViewModel.removeContentResponse.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }

                is ResultType.Success<*> -> {
                    val response = result.data as RemoveListResponse
                    if (response.status == "success") {
                        binding.progressbar.visibility = View.GONE
                        Toast.makeText(
                            this@FavoriteListActivity,
                            "remove list ${response.status}",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (username.toString().isNotEmpty()) {
                            favoriteListViewModel.fetchFavoriteContent(username.toString(), "1")
                        }
                    } else {
                        Toast.makeText(
                            this@FavoriteListActivity,
                            response.status,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressbar.visibility = View.GONE
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@FavoriteListActivity,
                        "Something is wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        favoriteListViewModel.favoriteContent.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }

                is ResultType.Success<*> -> {
                    val content = result.data as FavoriteResponse
                    if (content.contents.isNotEmpty()) {
                        binding.progressbar.visibility = View.GONE
                        binding.emptyResultTv.visibility = View.GONE
                        favoriteListAdapter.content = content.contents
                        favoriteListAdapter.notifyDataSetChanged()
                    } else {
                        favoriteListAdapter.content = emptyList()
                        favoriteListAdapter.notifyDataSetChanged()
                        binding.emptyResultTv.visibility = View.VISIBLE
                        binding.emptyResultTv.text = "No favorite item available"
                        binding.progressbar.visibility = View.GONE
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@FavoriteListActivity,
                        "Something is wrong. Please try again",
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
        if (username.toString().isNotEmpty()) {
            removeListViewModel.removeFavoriteContent(contentId, username.toString())
        }

    }

    private fun loadMoreData() {

        val username = SharedPreferencesUtil.getData(this, UsernameInputKey, "").toString()

        favoriteListViewModel.fetchFavoriteContent(username.toString(), currentPage.toString())

        favoriteListViewModel.favoriteContent.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
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
                            }
                        }
                        binding.progressbar.visibility = View.GONE
                        binding.emptyResultTv.visibility = View.GONE
                        //  favoriteListAdapter.content = content.contents
                        favoriteListAdapter.notifyDataSetChanged()
                    } else {
                        favoriteListAdapter.content = emptyList()
                        favoriteListAdapter.notifyDataSetChanged()
                        binding.emptyResultTv.visibility = View.VISIBLE
                        binding.emptyResultTv.text = "No favorite item available"
                        binding.progressbar.visibility = View.GONE
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@FavoriteListActivity,
                        "Something is wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun onItemClickListener(position: Int, item: Content?) {
        val username = SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "")

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

}