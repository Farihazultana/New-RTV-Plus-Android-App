package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rtv_plus_android_app_revamp.data.models.search.SearchResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivitySearchBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.SearchListAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SearchViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var searchListAdapter: SearchListAdapter
    private var searchQuery: String? = null
    private var voiceSearchQuery: String? = null
    val handler = Handler(Looper.getMainLooper())
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

        val closeIcon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeIcon.visibility = View.GONE

        val searchPlate: View = searchView.findViewById(androidx.appcompat.R.id.search_plate)
        searchPlate.setBackgroundResource(android.R.color.transparent)

        searchView.setOnCloseListener {
            val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            voiceIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            startActivityForResult(voiceIntent, REQUEST_VOICE_SEARCH)

            false
        }

        binding.searchVoiceBtn.visibility = View.VISIBLE
        binding.cancelButton.setOnClickListener {
            binding.searchView.setQuery("", false)
        }
        binding.searchVoiceBtn.setOnClickListener {
            val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            voiceIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            startActivityForResult(voiceIntent, REQUEST_VOICE_SEARCH)
        }

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchViewModel.fetchSearchData("app", query.toString())

                if (!query.isNullOrEmpty()) {
                    searchViewModel.fetchSearchData("app", query.toString())
                    Toast.makeText(this@SearchActivity, "Search Query: $query", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    binding.searchVoiceBtn.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.GONE
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    searchQuery = newText
                    if (newText.length > 2) {
                        handler.postDelayed({
                            searchViewModel.fetchSearchData("app", newText.toString())
                            binding.progressbar.visibility = View.VISIBLE
                        }, 1500)
                    }
                    binding.searchVoiceBtn.visibility = View.GONE
                    binding.cancelButton.visibility = View.VISIBLE

                    Toast.makeText(
                        this@SearchActivity,
                        "Current Query: $newText",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.searchVoiceBtn.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.GONE
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
                        if (!searchQuery.isNullOrEmpty()) {
                            binding.emptyResultTv.text = "No result found for: $searchQuery"
                            searchQuery = null
                        }
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
            if (results != null && results.isNotEmpty()) {
                // Get the first recognized voice input and save it in the variable
                voiceSearchQuery = results[0]
                searchViewModel.fetchSearchData("app", voiceSearchQuery.toString())
                if (!voiceSearchQuery.isNullOrEmpty()) {
                    binding.emptyResultTv.text = "No result found for: $voiceSearchQuery"
                    binding.searchView.setQuery(voiceSearchQuery, true)
                    voiceSearchQuery = null
                }
            }
        }
    }

    companion object {
        const val REQUEST_VOICE_SEARCH = 123
    }

}