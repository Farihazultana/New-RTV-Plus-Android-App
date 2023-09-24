package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.single_content.single.SingleContentResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivityPlayerBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.ParentHomeAdapter
import com.example.rtv_plus_android_app_revamp.ui.adapters.SimilarItemsAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SingleContentViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val singleContentViewModel by viewModels<SingleContentViewModel>()
    private lateinit var similarItemsAdapter: SimilarItemsAdapter
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val receivedValue = intent.getStringExtra("id")
        if (receivedValue != null) {
            singleContentViewModel.fetchSingleContent("8801919276405", receivedValue.toString(), "web")
        }

        similarItemsAdapter = SimilarItemsAdapter(emptyList())
        binding.similarItemRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.similarItemRecyclerView.adapter = similarItemsAdapter

        singleContentViewModel.content.observe(this, Observer { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.nastedScrollView.visibility = View.GONE
                    // Handle loading state if needed
                }
                is ResultType.Success<*> -> {
                    val content = result.data as SingleContentResponse
                    binding.progressbar.visibility = View.GONE
                    binding.nastedScrollView.visibility = View.VISIBLE

                    Glide.with(binding.imageView.context)
                        .load(content.image_location)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(binding.imageView)

                    binding.title.text = content.name
                    binding.releaseYear.text = "Release Year: ${content.released}"
                    binding.type.text = content.type
                    binding.length.text = content.length2

                    if (!content.similar.isNullOrEmpty()) {


                        similarItemsAdapter.similarContentList = content.similar[0].similarcontents
                        binding.progressBar.visibility = View.GONE
                        similarItemsAdapter.notifyDataSetChanged()


                      //  Log.e("ssssssssssssssssssss", "item activity ${content.similar}")
                    } else {
                        Log.e("ssssssssssssssssssss", "item empty")
                    }

                    Log.e("dddddddddddddddddddddddddd", content.toString())
                }
                is ResultType.Error -> {
                    val errorMessage = result.exception.message
                    Toast.makeText(this@PlayerActivity, "Something is wrong. Please try again", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}