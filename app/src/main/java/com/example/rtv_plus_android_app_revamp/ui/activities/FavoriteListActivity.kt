package com.example.rtv_plus_android_app_revamp.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.FavoriteResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivityFavoriteListBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.FavoriteListAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.FavoriteListViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListActivity : AppCompatActivity() {
    lateinit var binding: ActivityFavoriteListBinding
    private val favoriteListViewModel by viewModels<FavoriteListViewModel>()
    private lateinit var favoriteListAdapter: FavoriteListAdapter
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

        favoriteListAdapter = FavoriteListAdapter(emptyList())
        binding.favouriteListRecyclerview.layoutManager = GridLayoutManager(this, 2)
        binding.favouriteListRecyclerview.adapter = favoriteListAdapter

        favoriteListViewModel.fetchFavoriteContent("8801825414747", "1")

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
                        binding.emptyResultTv.visibility = View.VISIBLE
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

}