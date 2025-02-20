package com.rtvplus.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rtvplus.R
import com.rtvplus.databinding.FragmentHomeBinding
import com.rtvplus.ui.activities.MainActivity
import com.rtvplus.ui.activities.SearchActivity
import com.rtvplus.ui.adapters.ParentHomeAdapter
import com.rtvplus.ui.viewmodels.HomeViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.isLoggedIn
import com.rtvplus.utils.AppUtils.isPostPlayTime
import com.rtvplus.utils.LogInUtil
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.rtvplus.utils.SocialmediaLoginUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment(),LogInUtil.ObserverListener,SocialmediaLoginUtil.ObserverListenerSocial {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentHomeAdapter: ParentHomeAdapter
    private val homeViewModel by viewModels<HomeViewModel>()
    var username: String? = ""
    private lateinit var signInType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        signInType = SharedPreferencesUtil.getData(requireActivity(), AppUtils.SignInType, "").toString()
        if (signInType == "Phone") {
            LogInUtil().observeLoginData(requireActivity(),this,this,this)
        } else {
            SocialmediaLoginUtil().observeSocialLogInData(requireActivity(),this,this,this)
        }


        if (!AppUtils.isOnline(requireContext())) {
            AppUtils.showAlertDialog(requireContext())
        }

        username =
            SharedPreferencesUtil.getData(requireContext(), AppUtils.UsernameInputKey, "")
                .toString()

        binding.searchIcon.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        binding.tryAgainBtn.setOnClickListener {
            homeViewModel.fetchHomeData(username!!, "home", "3", "app", "en")
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }




        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.fetchHomeData(username!!, "home", "3", "app", "en")

        parentHomeAdapter =
            ParentHomeAdapter(requireContext(), emptyList(), lifecycle, this)
        binding.parentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.parentRecyclerview.adapter = parentHomeAdapter

//        var backPressedTime: Long = 0
//
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (backPressedTime + 2000 > System.currentTimeMillis()) {
//                    requireActivity().finish()
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        "Press back again to leave the app.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//                backPressedTime = System.currentTimeMillis()
//            }
//        }
//
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.homeData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.tryAgainBtn.visibility = View.GONE
                        binding.shimmerFrameLayout.startShimmer()
                        binding.shimmerFrameLayout.visibility = View.VISIBLE
                    }

                    is ResultType.Success -> {
                        val homeData = result.data
                        parentHomeAdapter.homeData = homeData.data
                        binding.tryAgainBtn.visibility = View.GONE
                        binding.parentRecyclerview.visibility = View.VISIBLE
                        parentHomeAdapter.notifyDataSetChanged()
                        binding.shimmerFrameLayout.stopShimmer()
                        binding.shimmerFrameLayout.visibility = View.GONE
                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            requireContext(),
                            R.string.error_response_msg,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.tryAgainBtn.visibility = View.VISIBLE
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()

                    }
                }
            }
        }
    }

    // Function to bind a fragment to the FragmentContainerView
    fun bindFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(android.R.id.content, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
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

        Log.e("checkLogin", isLoggedIn.toString())
        Log.e("checkLogin", isPostPlayTime.toString())

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
        bottomNavigationView.selectedItemId = R.id.HomeFragment

        homeViewModel.fetchHomeData(username!!, "home", "3", "app", "en")

    }

    override fun observerListener(result: String) {

    }

    override fun observerListenerSocial(result: String, loginSrc: String) {

    }

}