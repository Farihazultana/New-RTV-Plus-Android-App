package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.AddListResponse
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.RemoveListResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.playlist.PlayListResponse
import com.example.rtv_plus_android_app_revamp.data.models.single_content.single.SingleContentResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivityPlayerBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.PlayListAdapter
import com.example.rtv_plus_android_app_revamp.ui.adapters.SimilarItemsAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.AddFavoriteListViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.PlayListViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.RemoveFavoriteListViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SingleContentViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val singleContentViewModel by viewModels<SingleContentViewModel>()
    private val playListViewModel by viewModels<PlayListViewModel>()
    private val addListViewModel by viewModels<AddFavoriteListViewModel>()
    private val removeListViewModel by viewModels<RemoveFavoriteListViewModel>()
    private lateinit var similarItemsAdapter: SimilarItemsAdapter
    private lateinit var playListAdapter: PlayListAdapter
    private lateinit var player: ExoPlayer
    var clickedItemIndex = 0

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val receivedValue = intent.getStringExtra("id")
        val contentType = intent.getStringExtra("type")

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        binding.title.isSelected = true
        binding.title.isFocusable = true
        binding.title.isFocusableInTouchMode = true

        player = ExoPlayer.Builder(this).setAudioAttributes(
            androidx.media3.common.AudioAttributes.DEFAULT, true
        ).setHandleAudioBecomingNoisy(true).build()

        binding.playerView.player = player

        if (isFullscreen()) {
            binding.playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            binding.playerView.layoutParams.height =
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._180sdp)

        }
        val button: ImageView = findViewById(R.id.fullscreen)

        button.setOnClickListener {
            val isFullscreen = isFullscreen()
            setFullscreen(!isFullscreen)
        }

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
                            .placeholder(R.drawable.no_img).into(binding.imageView)

                        binding.suggestionTitle.text = content.similar[0].similarcatname
                        binding.title.text = content.name
                        binding.releaseYear.text = "Release Year: ${content.released}"
                        binding.type.text = content.type
                        binding.length.text = content.length2

                        // Build the media item.
                        val mediaItem = MediaItem.fromUri(content.url)
                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.play()

                        player.addListener(object : Player.Listener {
                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                if (isPlaying) {
                                    // Active playback.
                                } else {
                                    // Not playing because playback is paused, ended, suppressed, or the player
                                    // is buffering, stopped or failed. Check player.playWhenReady,
                                    // player.playbackState, player.playbackSuppressionReason and
                                    // player.playerError for details.
                                }
                            }
                        })

                        var isInList = 0

                        if (isInList == 0) {
                            binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_border_24)
                        } else {
                            binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                        }

                        binding.favouriteIcon.setOnClickListener {

                            if (isInList == 1) {
                                removeListViewModel.removeFavoriteContent(
                                    content.id,
                                    "8801825414747"
                                )
                            } else {
                                addListViewModel.addFavoriteContent(content.id, "8801825414747")
                            }

                        }


                        binding.commentIcon.setOnClickListener {
                            // Inflate the custom layout for the AlertDialog
                            val inflater = LayoutInflater.from(this)
                            val customDialogView = inflater.inflate(R.layout.comment_custom_alert_dialog, null)

                            // Find views in the custom layout
                            val editText = customDialogView.findViewById<EditText>(R.id.editText)
                            val confirmButton = customDialogView.findViewById<Button>(R.id.submitComment)

                            // Create the AlertDialog
                            val alertDialogBuilder = AlertDialog.Builder(this)
                            alertDialogBuilder.setView(customDialogView)
                            val alertDialog = alertDialogBuilder.create()

                            // Set a click listener for the Confirm button
                            confirmButton.setOnClickListener {
                                // Handle the input from the EditText here
                                val userInput = editText.text.toString()
                                // Do something with the user input

                                // Dismiss the AlertDialog after handling the input
                                alertDialog.dismiss()
                            }
                            alertDialog.show()
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
                                            this@PlayerActivity,
                                            response.status,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                                        isInList = 1

                                    } else {
                                        Toast.makeText(
                                            this@PlayerActivity,
                                            response.status,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.progressbar.visibility = View.GONE
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

                        addListViewModel.addContentResponse.observe(this) { result ->
                            when (result) {
                                is ResultType.Loading -> {
                                    binding.progressbar.visibility = View.VISIBLE
                                }

                                is ResultType.Success<*> -> {
                                    val response = result.data as AddListResponse
                                    if (response.status == "success") {
                                        binding.progressbar.visibility = View.GONE
                                        Toast.makeText(
                                            this@PlayerActivity,
                                            response.status,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                                        isInList = 1

                                    } else {
                                        Toast.makeText(
                                            this@PlayerActivity,
                                            response.status,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.progressbar.visibility = View.GONE
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

                        binding.shareIcon.setOnClickListener {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            val shareMessage = content.sharable
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(Intent.createChooser(shareIntent, "Share via"))
                        }

                        if (content.similar.isNotEmpty()) {
                            binding.similarItemRecyclerView.adapter = similarItemsAdapter
                            similarItemsAdapter.similarContentList =
                                content.similar[0].similarcontents
                            binding.progressBar.visibility = View.GONE
                            similarItemsAdapter.notifyDataSetChanged()
                        } else {
                            Log.d("ssssssssssssssssssss", "item empty")
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
            binding.favouriteIcon.visibility = View.GONE

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

                            var mediaItem =
                                MediaItem.fromUri(content.episodelist[clickedItemIndex].freeurl)
                            player.setMediaItem(mediaItem)
                            player.prepare()
                            player.play()

                            playListAdapter.setOnItemClickListener(object :
                                PlayListAdapter.OnItemClickListener {
                                override fun onItemClick(position: Int) {
                                    // Update the clicked item's index
                                    clickedItemIndex = position
                                    mediaItem =
                                        MediaItem.fromUri(content.episodelist[clickedItemIndex].freeurl)
                                    player.setMediaItem(mediaItem)
                                    player.prepare()
                                    player.play()
                                }
                            })

                            binding.shareIcon.setOnClickListener {
                                val shareIntent = Intent(Intent.ACTION_SEND)
                                shareIntent.type = "text/plain"
                                val shareMessage = content.sharable
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                                startActivity(Intent.createChooser(shareIntent, "Share via"))
                            }

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

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }

    override fun onStop() {
        super.onStop()
        player.stop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        player.stop()
    }

    fun isFullscreen(): Boolean {
        return requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun setFullscreen(fullscreen: Boolean) {
        val playerView = binding.playerView
        val fullScreenbutton: ImageView = findViewById(R.id.fullscreen)

        if (fullscreen) {
            // Set the activity orientation to landscape
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            fullScreenbutton.setImageResource(R.drawable.baseline_fullscreen_exit_24)

            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

            playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            // Set the activity orientation back to portrait
            fullScreenbutton.setImageResource(R.drawable.baseline_fullscreen_24)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

            playerView.layoutParams.height =
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._180sdp)
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

}