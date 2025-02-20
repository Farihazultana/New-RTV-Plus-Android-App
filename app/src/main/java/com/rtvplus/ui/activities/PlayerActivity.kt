package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rtvplus.R
import com.rtvplus.data.models.comment.CommentResponse
import com.rtvplus.data.models.favorite_list.AddListResponse
import com.rtvplus.data.models.favorite_list.RemoveListResponse
import com.rtvplus.data.models.single_content.playlist.PlayListResponse
import com.rtvplus.data.models.single_content.single.Similarcontent
import com.rtvplus.data.models.single_content.single.SingleContentResponse
import com.rtvplus.databinding.ActivityPlayerBinding
import com.rtvplus.ui.adapters.PlayListAdapter
import com.rtvplus.ui.adapters.SimilarItemsAdapter
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.ui.viewmodels.AddFavoriteListViewModel
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.ui.viewmodels.PlayListViewModel
import com.rtvplus.ui.viewmodels.RemoveFavoriteListViewModel
import com.rtvplus.ui.viewmodels.SaveCommentViewModel
import com.rtvplus.ui.viewmodels.SavePlayTimeViewModel
import com.rtvplus.ui.viewmodels.SingleContentViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.isPostPlayTime
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(), SimilarItemsAdapter.itemClickListener {
    private lateinit var binding: ActivityPlayerBinding
    private val singleContentViewModel by viewModels<SingleContentViewModel>()
    private val playListViewModel by viewModels<PlayListViewModel>()
    private val addListViewModel by viewModels<AddFavoriteListViewModel>()
    private val removeListViewModel by viewModels<RemoveFavoriteListViewModel>()
    private val commentViewModel by viewModels<SaveCommentViewModel>()
    private lateinit var similarItemsAdapter: SimilarItemsAdapter
    private lateinit var playListAdapter: PlayListAdapter
    private lateinit var player: ExoPlayer
    var clickedItemIndex = 0
    lateinit var username: String
    private lateinit var receivedValue: String
    private lateinit var contentType: String
    private var startFrom: Long? = 0L
    private lateinit var catcode: String
    private lateinit var handler: Handler
    private var elapsedTime: Long = 0L
    private var time: Long = 0L
    private var isInList: Int = 0
    private val logInViewModel by viewModels<LogInViewModel>()
    private val savePlayTimeViewModel by viewModels<SavePlayTimeViewModel>()

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        if (!AppUtils.isOnline(this)) {
            AppUtils.showAlertDialog(this)
        }

        username = SharedPreferencesUtil.getData(
            this,
            AppUtils.UsernameInputKey,
            ""
        ).toString()
        receivedValue = intent.getStringExtra("id").toString()
        contentType = intent.getStringExtra("type").toString()
        catcode = intent.getStringExtra("ct").toString()

        isPostPlayTime = false

        val isPlayed = intent.getStringExtra("isplayed")
        if (!isPlayed.isNullOrEmpty()) {
            startFrom = isPlayed.toLong()
        }

        initilizeSimilarContentAdapter()

        keepScreenOn()

        initilizePlayer()

        handleFullScreen()

        checkResponse()

        if (username.isNotEmpty()) {
            logInViewModel.fetchLogInData(username, "", "yes", "1")
        }

        checkIfPremiumUser()

        if (contentType == "single") {
            playSingleContent()
        } else {
            playPlayListContent()
        }


        binding.favouriteIcon.setOnClickListener {

            if (isInList == 1) {
                if (username.isNotEmpty()) {
                    removeListViewModel.removeFavoriteContent(receivedValue, username)
                }
            } else {
                if (username.isNotEmpty()) {
                    addListViewModel.addFavoriteContent(receivedValue, username)
                }
            }
        }

        handler = Handler()
        startTimer()

    }

    private fun hideStatusBar() {

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }

    }


    private fun initilizeSimilarContentAdapter() {
        similarItemsAdapter = SimilarItemsAdapter(emptyList(), this, checkIfPremiumUser())
        playListAdapter = PlayListAdapter(emptyList())
        binding.similarItemRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun handleFullScreen() {
        val fullScreenButton: ImageView = findViewById(R.id.fullscreen)
        fullScreenButton.setOnClickListener {
            val isFullscreen = isFullscreen()
            setFullscreen(!isFullscreen)
        }
    }

    private fun initilizePlayer() {
        player = ExoPlayer.Builder(this).setAudioAttributes(
            AudioAttributes.DEFAULT, true
        ).setHandleAudioBecomingNoisy(true).build()
        binding.playerView.player = player
    }

    private fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun checkIfPremiumUser(): Int {
        var isPremiumUser: Int? = 0

        logInViewModel.logInData.observe(this) {
            when (it) {
                is ResultType.Success -> {
                    val logInResult = it.data

                    for (item in logInResult) {
                        val result = item.play
                        isPremiumUser = result
                        similarItemsAdapter.isPemiumUser = isPremiumUser
                    }
                }

                is ResultType.Error -> {
                    isPremiumUser = 0
                }

                else -> {
                    isPremiumUser = 0
                }
            }
        }

        return isPremiumUser!!
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun playPlayListContent() {
        updateUiForPlayList()
        if (username.isNotEmpty()) {
            playListViewModel.fetchPlayListContent(
                username,
                receivedValue,
                "hd",
                catcode
            )
        }
        playListViewModel.content.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                    binding.shimmerFrameLayout.startShimmer()
                    binding.nastedScrollView.visibility = View.GONE
                }

                is ResultType.Success<*> -> {
                    val content = result.data as PlayListResponse
                    binding.shimmerFrameLayout.visibility = View.GONE
                    binding.shimmerFrameLayout.stopShimmer()
                    binding.nastedScrollView.visibility = View.VISIBLE
                    binding.suggestionTitle.text = content.dramaname

                    Glide.with(binding.imageView.context).load(content.dramacover)
                        .placeholder(R.drawable.ic_launcher_background).into(binding.imageView)

                    binding.title.text = content.dramaname
                    binding.episodeNum.visibility = View.VISIBLE
                    binding.episodeNum.text = "${content.episodelist.size} Episodes"

                    if (content.episodelist.isNotEmpty()) {
                        binding.similarItemRecyclerView.adapter = playListAdapter
                        buildMediaItem(content.episodelist[clickedItemIndex].freeurl)
                        playListAdapter.setOnItemClickListener(object :
                            PlayListAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                clickedItemIndex = position
                                buildMediaItem(content.episodelist[clickedItemIndex].freeurl)
                            }
                        })
                        binding.shareIcon.setOnClickListener {
                            shareContent(content.sharable)
                        }
                        binding.commentIcon.setOnClickListener {
                            // Inflate the custom layout for the AlertDialog
                            displayCommentScreen()
                        }
                        playListAdapter.episodeList = content.episodelist
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()
                        similarItemsAdapter.notifyDataSetChanged()
                    } else {
                        Log.d("checkEmpty", "item empty")
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@PlayerActivity,
                        R.string.error_response_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun shareContent(content: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun updateUiForPlayList() {
        binding.title.visibility = View.GONE
        binding.releaseYear.visibility = View.GONE
        binding.type.visibility = View.GONE
        binding.length.visibility = View.GONE
        binding.imageView.visibility = View.GONE
        binding.favouriteIcon.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun playSingleContent() {
        if (username.isNotEmpty()) {
            singleContentViewModel.fetchSingleContent(
                username, receivedValue, "app"
            )
        }
        singleContentViewModel.content.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                    binding.shimmerFrameLayout.startShimmer()
                    binding.nastedScrollView.visibility = View.GONE
                    // Handle loading state if needed
                }

                is ResultType.Success<*> -> {
                    val content = result.data as SingleContentResponse
                    binding.shimmerFrameLayout.visibility = View.GONE
                    binding.shimmerFrameLayout.stopShimmer()
                    binding.nastedScrollView.visibility = View.VISIBLE

                    Glide.with(binding.imageView.context).load(content.image_location)
                        .placeholder(R.drawable.no_img).into(binding.imageView)

                    binding.suggestionTitle.text = content.similar[0].similarcatname
                    binding.title.text = content.name
                    binding.releaseYear.text = "Release Year: ${content.released}"
                    binding.type.text = content.type
                    binding.length.text = content.length2

                    // Build the media item.
                    buildMediaItem(content.url)

                    isInList = content.mylist

                    if (isInList == 1) {
                        binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                    } else {
                        binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_border_24)
                    }

//                    binding.favouriteIcon.setImageResource(
//                        if (isInList == 0) {
//                            R.drawable.baseline_favorite_border_24
//                        } else {
//                            R.drawable.baseline_favorite_24
//                        }
//                    )

//                    binding.favouriteIcon.setOnClickListener {
//
//                        if (isInList == 1) {
//
//                            if (username.isNotEmpty()) {
//                                removeListViewModel.removeFavoriteContent(
//                                    content.id,
//                                    username
//                                )
//                            }
//                        }
//                        else {
//                            binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_border_24)
//                            if (username.isNotEmpty()) {
//                                addListViewModel.addFavoriteContent(
//                                    content.id,
//                                    username
//                                )
//                            }
//
//                        }
//
//                    }

                    binding.commentIcon.setOnClickListener {
                        // Inflate the custom layout for the AlertDialog
                        displayCommentScreen()
                    }

                    binding.shareIcon.setOnClickListener {
                        shareContent(content.sharable)
                    }

                    removeListViewModel.removeContentResponse.observe(this) {
                        when (it) {
                            is ResultType.Loading -> {
                                binding.shimmerFrameLayout.visibility = View.VISIBLE
                                binding.shimmerFrameLayout.startShimmer()

                            }

                            is ResultType.Success<*> -> {
                                val response = it.data as RemoveListResponse
                                if (response.status == "success") {
                                    binding.shimmerFrameLayout.visibility = View.GONE
                                    binding.shimmerFrameLayout.stopShimmer()
                                    binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_border_24)
                                    isInList = 0

                                } else {

                                    binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                                    binding.shimmerFrameLayout.visibility = View.GONE
                                    binding.shimmerFrameLayout.stopShimmer()
                                }
                            }

                            is ResultType.Error -> {
                                Toast.makeText(
                                    this@PlayerActivity,
                                    R.string.error_response_msg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    addListViewModel.addContentResponse.observe(this) { it ->
                        when (it) {
                            is ResultType.Loading -> {
                                binding.shimmerFrameLayout.visibility = View.VISIBLE
                                binding.shimmerFrameLayout.startShimmer()
                            }

                            is ResultType.Success<*> -> {
                                val response = it.data as AddListResponse
                                if (response.status == "success") {
                                    binding.shimmerFrameLayout.visibility = View.GONE
                                    binding.shimmerFrameLayout.stopShimmer()
                                    binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                                    isInList = 1

                                } else {
                                    binding.favouriteIcon.setImageResource(R.drawable.baseline_favorite_border_24)
                                    binding.shimmerFrameLayout.visibility = View.GONE
                                    binding.shimmerFrameLayout.stopShimmer()
                                }
                            }

                            is ResultType.Error -> {
                                Toast.makeText(
                                    this@PlayerActivity,
                                    R.string.error_response_msg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }


                    if (content.similar.isNotEmpty()) {
                        displaySimilarContent(content)
                    } else {
                        Log.d("checkEmpty", "item empty")
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@PlayerActivity,
                        R.string.error_response_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun buildMediaItem(content: String) {
        val mediaItem = MediaItem.fromUri(content)
        player.setMediaItem(mediaItem)
        player.prepare()
        Log.e("playTime", startFrom.toString())
        player.seekTo(startFrom!!)
        player.play()
    }

    private fun startTimer() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (player.isPlaying) {
                    elapsedTime += 1000 // Add 1 second in milliseconds to elapsedTime
                }
                time += 1000 // Add 1 second in milliseconds to time

                handler.postDelayed(this, 1000) // Update every second
            }
        }, 1000) // Initial delay of 1 second
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun displaySimilarContent(content: SingleContentResponse) {
        binding.similarItemRecyclerView.adapter = similarItemsAdapter
        similarItemsAdapter.similarContentList =
            content.similar[0].similarcontents
        similarItemsAdapter.isPemiumUser = checkIfPremiumUser()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.shimmerFrameLayout.stopShimmer()
        similarItemsAdapter.notifyDataSetChanged()
    }

    private fun displayCommentScreen() {
        val inflater = LayoutInflater.from(this)
        val customDialogView =
            inflater.inflate(R.layout.comment_custom_alert_dialog, null)

        // Find views in the custom layout
        val editText =
            customDialogView.findViewById<EditText>(R.id.contentEditText)
        val confirmButton =
            customDialogView.findViewById<Button>(R.id.submitComment)

        // Create the AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(customDialogView)
        val alertDialog = alertDialogBuilder.create()

        // Set a click listener for the Confirm button
        confirmButton.setOnClickListener {
            val userInput = editText.text.toString()

            if (userInput.length < 3)
            {
                Toast.makeText(this, "Type at least 3 character",Toast.LENGTH_SHORT).show()
            }
            else
            {
                commentViewModel.saveComment(username, userInput)
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun checkResponse() {

        commentViewModel.saveCommentResponse.observe(this) { it ->
            when (it) {
                is ResultType.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }

                is ResultType.Success<*> -> {
                    val response = it.data as CommentResponse
                    if (response.status == "success") {
                        binding.progressbar.visibility = View.GONE
                    }
                }

                is ResultType.Error -> {
                    binding.progressbar.visibility = View.GONE
                }
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    fun isFullscreen(): Boolean {
        return requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    }

    fun setFullscreen(fullscreen: Boolean) {
        val playerView = binding.playerView
        val fullScreenbutton: ImageView = findViewById(R.id.fullscreen)

        if (fullscreen) {
            makeFullScreen(fullScreenbutton, playerView)
            // hideStatusBar()
        } else {
            // hideStatusBar()
            // Set the activity orientation back to portrait
            fullScreenbutton.setImageResource(R.drawable.baseline_fullscreen_24)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            WindowInsetsControllerCompat(
                this.window,
                this.window.decorView
            ).show(WindowInsetsCompat.Type.systemBars())
            playerView.layoutParams.height =
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._180sdp)
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun makeFullScreen(
        fullScreenbutton: ImageView,
        playerView: PlayerView
    ) {
        //   hideStatusBar()
        // Set the activity orientation to landscape
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        fullScreenbutton.setImageResource(R.drawable.baseline_fullscreen_exit_24)

        WindowInsetsControllerCompat(this.window, this.window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }

        playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onItemClickListener(position: Int, item: Similarcontent?) {
        if (username.isNotEmpty()) {
            if (checkIfPremiumUser() == 0 && item?.isfree == "0") {

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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        player.stop()
        handler.removeCallbacksAndMessages(null)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        player.stop()
        val elapsedTimeInSeconds = (elapsedTime) / 1000
        val timeInSeconds = player.currentPosition / 1000

        if (timeInSeconds > 5) {
            savePlayTimeViewModel.savePlayTime(
                timeInSeconds.toString(),
                receivedValue,
                username,
                elapsedTimeInSeconds.toString()
            )

            isPostPlayTime = true
        }

        //  Log.e("elapsedTime", elapsedTimeInSeconds.toString())
        Log.e("elapsedTime", timeInSeconds.toString())

        val fragmentManager = supportFragmentManager
        val backStackEntryCount = fragmentManager.backStackEntryCount

        if (backStackEntryCount > 0) {
            // Pop the fragment on the first back button click
            fragmentManager.popBackStack()
            handler.removeCallbacksAndMessages(null)
        } else {
            // If the back stack is empty, navigate back or exit the activity
            super.onBackPressed()
        }
    }

}