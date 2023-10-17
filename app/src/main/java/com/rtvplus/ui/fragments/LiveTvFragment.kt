package com.rtvplus.ui.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.Navigation.findNavController
import com.rtvplus.R
import com.rtvplus.databinding.FragmentLiveTvBinding
import com.rtvplus.ui.activities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.SharedPreferencesUtil

class LiveTvFragment : Fragment() {
    private lateinit var binding: FragmentLiveTvBinding
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {

        val username = SharedPreferencesUtil.getData(requireContext(), AppUtils.UsernameInputKey, "").toString()

        if (username.isEmpty()) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLiveTvBinding.inflate(inflater, container, false)
        val view = binding.root

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                player.stop()
                val mainActivity = requireActivity() as MainActivity
                mainActivity.showBottomNavigationBar()

                val navController = findNavController(binding.root)
                navController.navigate(R.id.HomeFragment)
                val bottomNavigationView =
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
                bottomNavigationView.selectedItemId = R.id.HomeFragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        player = ExoPlayer.Builder(requireContext()).setAudioAttributes(
            androidx.media3.common.AudioAttributes.DEFAULT, true
        ).setHandleAudioBecomingNoisy(true).build()

        binding.playerView.player = player

        binding.fullScreenBtnTV.setOnClickListener {
            val isFullscreen = isFullscreen()
            setFullscreen(!isFullscreen)
        }
        val fullScreenbutton: ImageView = view.findViewById(R.id.fullscreen)

        fullScreenbutton.setOnClickListener {
            val isFullscreen = isFullscreen()
            setFullscreen(!isFullscreen)
        }

        val mediaItem =
            MediaItem.fromUri("https://streamingengine.rtvplus.tv/rtv720/mp4:RTHe9apWCX6.mp4/playlist.m3u8")
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()



        return view
    }

    fun isFullscreen(): Boolean {
        return requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun setFullscreen(fullscreen: Boolean) {
        val playerView = binding.playerView
        val fullScreenbutton: ImageView = requireActivity().findViewById(R.id.fullscreen)

        if (fullscreen) {
            // Set the activity orientation to landscape
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            fullScreenbutton.setImageResource(R.drawable.baseline_fullscreen_exit_24)

            requireActivity().window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)


            val mainActivity = requireActivity() as MainActivity
            mainActivity.hideBottomNavigationBar()

            playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            // Set the activity orientation back to portrait
            fullScreenbutton.setImageResource(R.drawable.baseline_fullscreen_24)
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            requireActivity().window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

            val mainActivity = requireActivity() as MainActivity
            mainActivity.showBottomNavigationBar()

            playerView.layoutParams.height =
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._180sdp)
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }

    override fun onStop() {
        super.onStop()
        player.stop()
    }

}