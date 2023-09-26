package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.single_content.playlist.PlayListResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.single.SingleContentResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivityPlayerBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.PlayListAdapter
import com.example.rtv_plus_android_app_revamp.ui.adapters.SimilarItemsAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.PlayListViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SingleContentViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val singleContentViewModel by viewModels<SingleContentViewModel>()
    private val playListViewModel by viewModels<PlayListViewModel>()
    private lateinit var similarItemsAdapter: SimilarItemsAdapter
    private lateinit var playListAdapter: PlayListAdapter

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val receivedValue = intent.getStringExtra("id")
        val contentType = intent.getStringExtra("type")

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                androidx.media3.common.AudioAttributes.DEFAULT, true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        binding.playerView.player = player

        val playerView = binding.playerView
        val button: ImageView = findViewById(R.id.fullscreen)
        button.setOnClickListener {
            val isFullscreen = isFullscreen()
            setFullscreen(!isFullscreen)
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
        }


//        val player = ExoPlayer.Builder(this).build()
//        binding.playerView.player = player
//
//        val mediaItem = MediaItem.Builder()
//            .setUri("https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4")
//            .setMimeType(MimeTypes.APPLICATION_MP4)
//            .build()
//
//        player.setMediaItem(mediaItem)


        if (receivedValue != null && contentType == "single") {
            singleContentViewModel.fetchSingleContent(
                "8801919276405", receivedValue.toString(), "web"
            )

            singleContentViewModel.content.observe(this) { result ->
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

                        Glide.with(binding.imageView.context).load(content.image_location)
                            .placeholder(R.drawable.ic_launcher_background).into(binding.imageView)

                        binding.title.text = content.name
                        binding.releaseYear.text = "Release Year: ${content.released}"
                        binding.type.text = content.type
                        binding.length.text = content.length2

                        // Build the media item.
                        val mediaItem =
                            MediaItem.fromUri("https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4")
                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.play()

                        player.addListener(
                            object : Player.Listener {
                                override fun onIsPlayingChanged(isPlaying: Boolean) {
                                    if (isPlaying) {
                                        // Active playback.
                                    } else {
                                        Toast.makeText(
                                            this@PlayerActivity,
                                            "Something is wrong, please try again",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Not playing because playback is paused, ended, suppressed, or the player
                                        // is buffering, stopped or failed. Check player.playWhenReady,
                                        // player.playbackState, player.playbackSuppressionReason and
                                        // player.playerError for details.
                                    }
                                }
                            }
                        )

                        if (content.similar.isNotEmpty()) {
                            binding.similarItemRecyclerView.adapter = similarItemsAdapter
                            similarItemsAdapter.similarContentList =
                                content.similar[0].similarcontents
                            binding.progressBar.visibility = View.GONE
                            similarItemsAdapter.notifyDataSetChanged()
                        } else {
                            Log.e("ssssssssssssssssssss", "item empty")
                        }
                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            this@PlayerActivity,
                            "Something is wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {

            binding.title.visibility = View.GONE
            binding.releaseYear.visibility = View.GONE
            binding.type.visibility = View.GONE
            binding.length.visibility = View.GONE
            binding.imageView.visibility = View.GONE


            playListViewModel.fetchPlayListContent("8801825414747", receivedValue.toString(), "hd")

            playListViewModel.content.observe(this) { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                        binding.nastedScrollView.visibility = View.GONE
                    }

                    is ResultType.Success<*> -> {
                        val content = result.data as PlayListResponse
                        binding.progressbar.visibility = View.GONE
                        binding.nastedScrollView.visibility = View.VISIBLE

                        binding.suggestionTitle.text = content.dramaname

                        Glide.with(binding.imageView.context).load(content.dramacover)
                            .placeholder(R.drawable.ic_launcher_background).into(binding.imageView)

                        binding.title.text = content.dramaname
                        binding.episodeNum.visibility = View.VISIBLE
                        binding.episodeNum.text = "${content.episodelist.size.toString()} Episodes"

                        if (content.episodelist.isNotEmpty()) {
                            binding.similarItemRecyclerView.adapter = playListAdapter
                            playListAdapter.episodeList = content.episodelist
                            binding.progressBar.visibility = View.GONE
                            similarItemsAdapter.notifyDataSetChanged()
                        } else {
                            Log.e("ssssssssssssssssssss", "item empty")
                        }
                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            this@PlayerActivity,
                            "Something is wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        similarItemsAdapter = SimilarItemsAdapter(emptyList())
        playListAdapter = PlayListAdapter(emptyList())
        binding.similarItemRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    fun isFullscreen(): Boolean {
        return requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun setFullscreen(fullscreen: Boolean) {
        val playerView = binding.playerView
        val button: ImageView = findViewById(R.id.fullscreen)
        if (fullscreen) {

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            button.visibility = View.GONE
            playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            button.visibility = View.VISIBLE
            playerView.layoutParams.height =
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._180sdp) // Replace with your desired height
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

}