package com.rtvplus.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.rtvplus.R
import com.rtvplus.data.models.search.Content
import com.rtvplus.data.models.search.SearchResponse
import com.rtvplus.databinding.ActivitySearchBinding
import com.rtvplus.ui.adapters.SearchListAdapter
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.ui.viewmodels.SearchViewModel
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.Type_fb
import com.rtvplus.utils.AppUtils.Type_google
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.AppUtils.isLoggedIn
import com.rtvplus.utils.LogInUtil
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.rtvplus.utils.SocialmediaLoginUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), SearchListAdapter.itemClickListener,  LogInUtil.ObserverListener, SocialmediaLoginUtil.ObserverListenerSocial {
    lateinit var binding: ActivitySearchBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var searchListAdapter: SearchListAdapter
    private var searchQuery: String? = null
    private var voiceSearchQuery: String? = null
    private val logInViewModel by viewModels<LogInViewModel>()
    private var isPremiumUser: Int? = 0
    val handler = Handler(Looper.getMainLooper())
    lateinit var username: String
    var searhText: String ?= ""
    private lateinit var signInType: String

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (!AppUtils.isOnline(this)) {
            AppUtils.showAlertDialog(this)
        }

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

        username = SharedPreferencesUtil.getData(this, UsernameInputKey, "").toString()



        signInType = SharedPreferencesUtil.getData(this, AppUtils.SignInType, "").toString()
        if (signInType == "Phone") {
            LogInUtil().observeLoginData(this, this, this, this)
        } else {
            SocialmediaLoginUtil().observeSocialLogInData(this, this, this, this)
        }

        isPremiumUser = SharedPreferencesUtil.getSavedLogInData(this)?.play ?: 0

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
                searhText = query.toString()
                searchViewModel.fetchSearchData("app", query.toString())

                if (!query.isNullOrEmpty()) {
                    searchViewModel.fetchSearchData("app", query.toString())
//                    Toast.makeText(this@SearchActivity, "Search Query: $query", Toast.LENGTH_SHORT)
//                        .show()
                } else {
                    binding.searchVoiceBtn.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.GONE
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (!AppUtils.isOnline(this@SearchActivity)) {
                    AppUtils.showAlertDialog(this@SearchActivity)
                }
                if (!newText.isNullOrEmpty()) {
                    searchQuery = newText
                    searhText = newText

                    if (newText.length > 2) {
                        handler.postDelayed({
                            searchViewModel.fetchSearchData("app", newText.toString())
                            binding.shimmerFrameLayout.visibility = View.VISIBLE
                            binding.shimmerFrameLayout.startShimmer()
                        }, 1000)
                    }
                    binding.searchVoiceBtn.visibility = View.GONE
                    binding.cancelButton.visibility = View.VISIBLE

                } else {
                    binding.searchVoiceBtn.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.GONE
                }
                return true
            }
        })
        searchListAdapter = SearchListAdapter(emptyList(), this, null)
        binding.searchItemRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.searchItemRecyclerView.adapter = searchListAdapter


        searchViewModel.searchData.observe(this) { result ->
            when (result) {
                is ResultType.Loading -> {
                }

                is ResultType.Success<*> -> {
                    isPremiumUser = SharedPreferencesUtil.getSavedLogInData(this)?.play ?: 0
                    val content = result.data as SearchResponse
                    if (content.contents.isNotEmpty()) {
                        if (isPremiumUser.toString().isNotEmpty()) {
                            binding.shimmerFrameLayout.stopShimmer()
                            binding.shimmerFrameLayout.visibility = View.GONE
                            binding.emptyResultTv.visibility = View.GONE
                            searchListAdapter.content = content.contents
                            searchListAdapter.isPemiumUser = isPremiumUser
                            searchListAdapter.notifyDataSetChanged()
                        }

                    } else {
                        binding.emptyResultTv.visibility = View.VISIBLE
                        if (!searchQuery.isNullOrEmpty()) {
                            binding.emptyResultTv.text = "No result found for: $searchQuery"
                            searchQuery = null
                        }
                        binding.shimmerFrameLayout.visibility = View.GONE
                        binding.shimmerFrameLayout.stopShimmer()
                        searchListAdapter.content = content.contents
                        searchListAdapter.notifyDataSetChanged()
                    }
                }

                is ResultType.Error -> {
                    Toast.makeText(
                        this@SearchActivity,
                        R.string.error_response_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VOICE_SEARCH && resultCode == Activity.RESULT_OK) {
            val results: ArrayList<String>? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null && results.isNotEmpty()) {
                // Get the first recognized voice input and save it in the variable
                voiceSearchQuery = results[0]
                searhText = voiceSearchQuery.toString()
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

    override fun onItemClickListener(position: Int, item: Content?) {
        if (item != null) {

            val phone = SharedPreferencesUtil.getData(this, AppUtils.LogInKey, "")

            if (phone.toString() != "") {
                if (isPremiumUser == 0 && item.isfree == "0") {

                    val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                    val subscriptionFragment = SubscriptionFragment()
                    fragmentTransaction.replace(R.id.subscriptionContainerView, subscriptionFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()

                } else {
                    val intent = Intent(this, PlayerActivity::class.java)
                        .putExtra("id", item.contentid)
                        .putExtra("type", "single")
                    startActivity(intent)
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val backStackEntryCount = fragmentManager.backStackEntryCount

        if (backStackEntryCount > 0) {
            // Pop the fragment on the first back button click
            fragmentManager.popBackStack()
        } else {
            // If the back stack is empty, navigate back or exit the activity
            super.onBackPressed()
        }
    }

    override fun onResume() {
        val loginData = SharedPreferencesUtil.getSavedSocialLogInData(this)
        val user = SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()

        Log.i("checkiflogin", isLoggedIn.toString())
        Log.i("checkiflogin", searhText.toString())
        if (isLoggedIn)
        {
            searchViewModel.fetchSearchData("app", searhText.toString())

            signInType = SharedPreferencesUtil.getData(this, AppUtils.SignInType, "").toString()
            if (signInType == "Phone") {
                LogInUtil().observeLoginData(this, this, this, this)
            } else {
                SocialmediaLoginUtil().observeSocialLogInData(this, this, this, this)
            }
        }

        if (signInType == "Phone") {
            val user =
                SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()
            val password =
                SharedPreferencesUtil.getData(this, AppUtils.UserPasswordKey, "").toString()
            LogInUtil().fetchLogInData(this, user, password)
        } else if (signInType== Type_google) {
            val user = SharedPreferencesUtil.getData(this, AppUtils.UsernameInputKey, "").toString()

            if(loginData != null){
                val email = loginData.email
                val firstname =loginData.firstName
                val lastname =loginData.lastName
                val imgUri =loginData.imageUri
                Log.i("OneTap", "onResume Search Activity: $user, $email, $firstname, $lastname, $imgUri")
                SocialmediaLoginUtil().fetchSocialLogInData(this,"google", user, firstname, lastname, email, imgUri)
            }

        }

        else if (signInType == Type_fb)
        {
            if (loginData != null) {
                val fullname = loginData.displayName
                val imgUrl = loginData.imageUri
                SocialmediaLoginUtil().fetchSocialLogInData(
                    this,
                    AppUtils.Type_fb,
                    user,
                    fullname,
                    "",
                    "",
                    imgUrl
                )
            }
        }

        super.onResume()
    }

    override fun observerListener(result: String) {

    }

    override fun observerListenerSocial(result: String, loginSrc: String) {

    }

}