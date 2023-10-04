package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.FavoriteResponse
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.RemoveListResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivityFavoriteListBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.FavoriteListAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.FavoriteListViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.RemoveFavoriteListViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListActivity : AppCompatActivity(), FavoriteListAdapter.OnRemoveItemClickListener {
    lateinit var binding: ActivityFavoriteListBinding
    private val favoriteListViewModel by viewModels<FavoriteListViewModel>()
    private val removeListViewModel by viewModels<RemoveFavoriteListViewModel>()
    private lateinit var favoriteListAdapter: FavoriteListAdapter

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

        favoriteListViewModel.fetchFavoriteContent("8801825414747", "1")


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
                        favoriteListViewModel.fetchFavoriteContent("8801825414747", "1")
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
                        Toast.makeText(this, "update data", Toast.LENGTH_SHORT).show()
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
        removeListViewModel.removeFavoriteContent(contentId, "8801825414747")
    }

}