package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.search.SearchResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.playlist.PlayListResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivitySearchBinding
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSearchBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.PlayListAdapter
import com.example.rtv_plus_android_app_revamp.ui.adapters.SearchListAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SearchViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SingleContentViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var searchListAdapter: SearchListAdapter
    private var searchQuery: String? = null
    private var voiceSearchQuery: String? = null

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }

        setContentView(view)
        val searchView = binding.searchView

        searchView.isIconified = false
        searchView.setOnCloseListener {
            // Code to be executed when the search view is closed (query cleared)
            Toast.makeText(
                this@SearchActivity,
                "Closed",
                Toast.LENGTH_SHORT
            ).show()

            val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            voiceIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            // Start the voice recognition activity
            startActivityForResult(voiceIntent, REQUEST_VOICE_SEARCH)

            false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchViewModel.fetchSearchData("app", query.toString())
                // This method is called when the user submits the query (for example, by pressing the search icon on the keyboard)
                if (!query.isNullOrEmpty()) {
                    Toast.makeText(this@SearchActivity, "Search Query: $query", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Handle empty query if needed
                }
                // Return true to indicate that the query has been handled
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // This method is called when the user changes the query text
                // You can handle query text changes here if needed
                if (!newText.isNullOrEmpty()) {
                    // Show a toast with the current query text
                    searchQuery = newText
                    searchViewModel.fetchSearchData("app", newText)
                    binding.progressbar.visibility = View.VISIBLE
                    Toast.makeText(
                        this@SearchActivity,
                        "Current Query: $newText",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                }
                return true
            }
        })

        searchListAdapter = SearchListAdapter(emptyList())
        binding.searchItemRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.searchItemRecyclerView.adapter = searchListAdapter

        searchViewModel.searchData.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                }

                is ResultType.Success<*> -> {
                    val content = result.data as SearchResponse
                    if (content.contents.isNotEmpty()) {
                        binding.progressbar.visibility = View.GONE
                        binding.emptyResultTv.visibility = View.GONE
                        searchListAdapter.content = content.contents
                        searchListAdapter.notifyDataSetChanged()
                    } else {
                        binding.emptyResultTv.visibility = View.VISIBLE
                        binding.emptyResultTv.text = "No result found for: $searchQuery"
                        binding.progressbar.visibility = View.GONE
                        searchListAdapter.content = content.contents
                        searchListAdapter.notifyDataSetChanged()
                    }

                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@SearchActivity,
                        "Something is wrong. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VOICE_SEARCH && resultCode == Activity.RESULT_OK) {
            val results: ArrayList<String>? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            // Check if there are recognized voice input results
            if (results != null && results.isNotEmpty()) {
                // Get the first recognized voice input and save it in the variable
                voiceSearchQuery = results[0]
                searchViewModel.fetchSearchData("app", voiceSearchQuery.toString())

                // Use voiceSearchQuery as needed (e.g., perform a search based on the voice input)
            }
        }
    }

    companion object {
        const val REQUEST_VOICE_SEARCH = 123
    }

}