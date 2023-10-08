package com.example.rtv_plus_android_app_revamp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.rtv_plus_android_app_revamp.databinding.ActivityLocalPaymentBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.LoginActivity.Companion.PhoneInputKey
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.LocalPaymentViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class LocalPaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocalPaymentBinding
    private val localPaymentViewModel by viewModels<LocalPaymentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val localPaymentView : WebView = binding.wvLocalPayment
        //localPaymentView.loadUrl("https://github.com/")

        val getPhoneNumSP = SharedPreferencesUtil.getData(
            this,
            PhoneInputKey,
            "defaultValue"
        )
        Log.i("Payment", "Showing Saved Phone Input from SP : $getPhoneNumSP")

        localPaymentViewModel.fetchLocalPaymentData("8801825414747", "start90")

        lifecycleScope.launch {
            localPaymentViewModel.localPaymentData.collect{
                when(it){
                    is ResultType.Loading ->{
                        binding.pbLoading.visibility = View.VISIBLE
                    }

                    is ResultType.Success ->{
                        binding.pbLoading.visibility = View.GONE
                        val result = it.data
                        for (item in result){
                            val url = item.sdpurl
                            localPaymentView.loadUrl(url)
                        }
                    }
                    else -> {}
                }
            }
        }

    }
}