package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rtvplus.data.models.favorite_list.FavoriteResponse
import com.rtvplus.data.models.favorite_list.RemoveListResponse
import com.rtvplus.databinding.ActivityFavoriteListBinding
import com.rtvplus.ui.adapters.FavoriteListAdapter
import com.rtvplus.ui.viewmodels.FavoriteListViewModel
import com.rtvplus.ui.viewmodels.RemoveFavoriteListViewModel
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListActivity : AppCompatActivity(), FavoriteListAdapter.OnRemoveItemClickListener {
    lateinit var binding: ActivityFavoriteListBinding
    private val favoriteListViewModel by viewModels<FavoriteListViewModel>()
    private val removeListViewModel by viewModels<RemoveFavoriteListViewModel>()
    private lateinit var favoriteListAdapter: FavoriteListAdapter
    var layoutManager = GridLayoutManager(this, 2)
    private var currentPage = 1
    private var isLoading = false
    private var isLastpage = false

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
        favoriteListAdapter = FavoriteListAdapter(null, this)
        binding.favouriteListRecyclerview.layoutManager = GridLayoutManager(this, 2)
        binding.favouriteListRecyclerview.adapter = favoriteListAdapter

        val username = SharedPreferencesUtil.getData(this, UsernameInputKey, "").toString()

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

}