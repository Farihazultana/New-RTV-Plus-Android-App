package com.example.rtv_plus_android_app_revamp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rtv_plus_android_app_revamp.data.models.local_payment.SaveLocalPaymentResponse
import com.example.rtv_plus_android_app_revamp.databinding.ActivityLocalPaymentBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.LoginActivity.Companion.PhoneInputKey
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.LocalPaymentViewModel
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SaveLocalPaymentViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocalPaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocalPaymentBinding
    private val localPaymentViewModel by viewModels<LocalPaymentViewModel>()
    val saveLocalPaymentViewModel by viewModels<SaveLocalPaymentViewModel>()

    companion object {
        lateinit var getPhoneNumSP: String
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val localPaymentView: WebView = binding.wvLocalPayment
        localPaymentView.settings.javaScriptEnabled = true
        localPaymentView.webViewClient =
            LocalPaymentWebViewClient(
                this,
                saveLocalPaymentViewModel
            ) // Pass the activity and ViewModel

        getPhoneNumSP = SharedPreferencesUtil.getData(
            this,
            PhoneInputKey,
            "defaultValue"
        ).toString()
        Log.i("Payment", "Showing Saved Phone Input from SP : $getPhoneNumSP")

        localPaymentViewModel.fetchLocalPaymentData(getPhoneNumSP, "start90")

        lifecycleScope.launch {
            localPaymentViewModel.localPaymentData.collect {
                when (it) {
                    is ResultType.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }

                    is ResultType.Success -> {
                        binding.pbLoading.visibility = View.GONE
                        val result = it.data
                        for (item in result) {
                            val url = item.sdpurl
                            localPaymentView.loadUrl(url)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    inner class LocalPaymentWebViewClient(
        private val activity: AppCompatActivity,
        private val saveLocalPaymentViewModel: SaveLocalPaymentViewModel
    ) : WebViewClient() {

        lateinit var paymentId: String
        lateinit var orderId: String


        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (shouldOpenInApp(url)) {
                if (url != null) {
                    view?.loadUrl(url)

                    if (url.contains("ACCEPTED")) {
                        val uri = Uri.parse(url)
                        val paramNames = uri.queryParameterNames
                        for (key in paramNames) {
                            if (key.contains("orderid")) {
                                orderId = uri.getQueryParameter(key)!!
                                Log.i("Payment", "shouldOverrideUrlLoading: $orderId")
                            }
                            if (key.contains("invoice")) {
                                paymentId = uri.getQueryParameter(key)!!
                                Log.i("Payment", "shouldOverrideUrlLoading: $paymentId")
                            }
                        }
                        if (paymentId != null && orderId != null) {
                            handleSavedLocalPaymentData(paymentId, orderId)
                        }
                        finish()
                    }
                }
                return true
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            view?.context?.startActivity(intent)

            return true
        }

        private fun shouldOpenInApp(url: String?): Boolean {
            return true
        }

        private fun handleSavedLocalPaymentData(paymentId: String, orderId: String) {
            saveLocalPaymentViewModel.fetchSavedLocalPaymentData(
                getPhoneNumSP,
                paymentId,
                orderId
            )
            lifecycleScope.launch {
                saveLocalPaymentViewModel.saveLocalPaymentData.observe(activity) {
                    when (it) {
                        is ResultType.Success<SaveLocalPaymentResponse> -> {
                            val savedLocalPayment = it.data.response
                            Toast.makeText(
                                this@LocalPaymentActivity,
                                savedLocalPayment,
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i(
                                "Payment",
                                "Saved Local payment data: $savedLocalPayment"
                            )
                        }

                        is ResultType.Error -> {
                            Toast.makeText(
                                this@LocalPaymentActivity,
                                "Something wend wrong, please try again!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@LocalPaymentActivity,
                                "Something wend wrong, please try again!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
