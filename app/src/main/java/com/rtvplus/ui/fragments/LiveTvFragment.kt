package com.rtvplus.ui.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.rtvplus.R
import com.rtvplus.databinding.FragmentLiveTvBinding
import com.rtvplus.ui.activities.MainActivity
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.LogInUtil
import com.rtvplus.utils.SharedPreferencesUtil
import com.rtvplus.utils.SocialmediaLoginUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LiveTvFragment : Fragment(), LogInUtil.ObserverListener,
    SocialmediaLoginUtil.ObserverListenerSocial {
    private lateinit var binding: FragmentLiveTvBinding
    private lateinit var player: ExoPlayer
    private val logInViewModel by viewModels<LogInViewModel>()
    lateinit var username: String
    private lateinit var signInType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!AppUtils.isOnline(requireContext())) {
            AppUtils.showAlertDialog(requireContext())
        }
//
//        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false)
//        val windowInsetsCompat =
//            WindowInsetsControllerCompat(requireActivity().getWindow(), requireActivity().getWindow().getDecorView())
//        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
//        windowInsetsCompat.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


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

        signInType =
            SharedPreferencesUtil.getData(requireActivity(), AppUtils.SignInType, "").toString()
        if (signInType == "Phone") {
            LogInUtil().observeLoginData(requireActivity(), this, this, this)
        } else {
            SocialmediaLoginUtil().observeSocialLogInData(requireActivity(), this, this, this)
        }


//        val fragmentManager = requireActivity().supportFragmentManager
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                player.stop()
//                fragmentManager.popBackStack()
//                //  requireActivity().finish()
//
////                val mainActivity = requireActivity() as MainActivity
////                mainActivity.showBottomNavigationBar()
//
////                val navController = findNavController(binding.root)
////                navController.navigate(R.id.HomeFragment)
////                val bottomNavigationView =
////                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
////                bottomNavigationView.selectedItemId = R.id.HomeFragment
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        player = ExoPlayer.Builder(requireContext()).setAudioAttributes(
            androidx.media3.common.AudioAttributes.DEFAULT, true
        ).setHandleAudioBecomingNoisy(true).build()

//        val player =
//            ExoPlayer.Builder(requireContext())
//                .setMediaSourceFactory(DefaultMediaSourceFactory(requireContext()).setLiveTargetOffsetMs(5000))
//                .build()

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
        val url = SharedPreferencesUtil.getSavedLogInData(requireActivity())?.liveurl ?: ""
        playLiveTv(url)

        return view
    }

    private fun playLiveTv(url: String) {
        Log.d("live-tv-url", url.toString())
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setLiveConfiguration(
                MediaItem.LiveConfiguration.Builder().setMaxPlaybackSpeed(1.02f).build()
            )
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

//        val mediaItem =
//            MediaItem.fromUri(url)
//        player.setMediaItem(mediaItem)
//        player.prepare()
//        player.play()
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
                controller.hide(WindowInsetsCompat.Type.statusBars())
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
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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

        signInType = SharedPreferencesUtil.getData(requireActivity(), AppUtils.SignInType, "").toString()
        val loginData = SharedPreferencesUtil.getSavedSocialLogInData(requireActivity())
        val user = SharedPreferencesUtil.getData(requireContext(), AppUtils.UsernameInputKey, "").toString()
        if (signInType == "Phone") {
            val password = SharedPreferencesUtil.getData(requireContext(),
                AppUtils.UserPasswordKey, "").toString()
            LogInUtil().fetchLogInData(this, user, password)
        } else if (signInType == AppUtils.Type_google){
            if (loginData != null){
                val email = loginData.email
                val firstname =loginData.firstName
                val lastname =loginData.lastName
                val imgUri =loginData.imageUri
                Log.i("OneTap", "onResume Subscription Fragment: $user, $email, $firstname, $lastname, $imgUri")
                SocialmediaLoginUtil().fetchSocialLogInData(this,AppUtils.Type_google, user, firstname, lastname, email, imgUri)
            }

        } else if (signInType == AppUtils.Type_fb){
            if(loginData != null){
                val fullname = loginData.displayName
                val imgUrl = loginData.imageUri
                SocialmediaLoginUtil().fetchSocialLogInData(this, AppUtils.Type_fb,user,fullname, "","", imgUrl )
            }

        }

        super.onResume()

    }

    override fun observerListener(result: String) {

    }


    override fun observerListenerSocial(result: String, loginSrc: String) {

    }

}