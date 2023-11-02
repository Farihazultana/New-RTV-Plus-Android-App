package com.rtvplus.ui.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.rtvplus.R
import com.rtvplus.databinding.FragmentLiveTvBinding
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.ui.activities.MainActivity
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LiveTvFragment : Fragment() {
    private lateinit var binding: FragmentLiveTvBinding
    private lateinit var player: ExoPlayer
    private val logInViewModel by viewModels<LogInViewModel>()
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {

        if (!AppUtils.isOnline(requireContext())) {
            AppUtils.showAlertDialog(requireContext())
        }

        val username =
            SharedPreferencesUtil.getData(requireContext(), AppUtils.UsernameInputKey, "")
                .toString()

        if (username.isEmpty()) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveTvBinding.inflate(inflater, container, false)
        val view = binding.root

        username = SharedPreferencesUtil.getData(requireContext(), AppUtils.UsernameInputKey, "")
            .toString()

        val fragmentManager = requireActivity().supportFragmentManager
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                player.stop()
                fragmentManager.popBackStack()
              //  requireActivity().finish()

//                val mainActivity = requireActivity() as MainActivity
//                mainActivity.showBottomNavigationBar()

//                val navController = findNavController(binding.root)
//                navController.navigate(R.id.HomeFragment)
//                val bottomNavigationView =
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
//                bottomNavigationView.selectedItemId = R.id.HomeFragment
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
        val url = getLiveTvUrl()
        playLiveTv(url)

        return view
    }

    private fun playLiveTv(url: String) {
        val mediaItem =
            MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun getLiveTvUrl(): String {
        if (username.isNotEmpty()) {
            logInViewModel.fetchLogInData(username, "", "yes", "1")
        }
        var url = ""

            logInViewModel.logInData.observe(viewLifecycleOwner) {
                url = when (it) {
                    is ResultType.Success -> {
                        it.data[0].liveurl
                    }
                    is ResultType.Error -> {
                        ""
                    }
                    else -> {
                        ""
                    }
                }
            }


        return url
    }

    private fun isFullscreen(): Boolean {
        return requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun setFullscreen(fullscreen: Boolean) {
        val playerView = binding.playerView
        val fullScreenbutton: ImageView = requireActivity().findViewById(R.id.fullscreen)

        if (fullscreen) {
            // Set the activity orientation to landscape
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            fullScreenbutton.setImageResource(R.drawable.baseline_fullscreen_exit_24)

            WindowInsetsControllerCompat(
                requireActivity().window,
                requireActivity().window.decorView
            ).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            val mainActivity = requireActivity() as MainActivity
            mainActivity.hideBottomNavigationBar()
            playerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        } else {
            // Set the activity orientation back to portrait
            fullScreenbutton.setImageResource(R.drawable.baseline_fullscreen_24)
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            WindowInsetsControllerCompat(
                requireActivity().window,
                requireActivity().window.decorView
            ).show(WindowInsetsCompat.Type.systemBars())
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showBottomNavigationBar()
            playerView.layoutParams.height =
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._180sdp)
            playerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    override fun onStop() {
        super.onStop()
        player.stop()
    }
    override fun onResume() {
        super.onResume()
        val username =
            SharedPreferencesUtil.getData(requireContext(), AppUtils.UsernameInputKey, "")
                .toString()

        if (username.isEmpty()) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

}