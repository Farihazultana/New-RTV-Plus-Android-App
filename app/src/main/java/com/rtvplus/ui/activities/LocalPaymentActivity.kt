package com.rtvplus.ui.activities

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
import com.rtvplus.data.models.local_payment.SaveLocalPaymentResponse
import com.rtvplus.databinding.ActivityLocalPaymentBinding
import com.rtvplus.ui.viewmodels.LocalPaymentViewModel
import com.rtvplus.ui.viewmodels.SaveLocalPaymentViewModel
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
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
            )

        getPhoneNumSP = SharedPreferencesUtil.getData(
            this,
            UsernameInputKey,
            "defaultValue"
        ).toString()
        Log.i("Payment", "Showing Saved Phone Input from SP : $getPhoneNumSP")

        val sub_pack = intent.getStringExtra("sub_pack")
        Log.i("Payment", "selected pack from Subscription Screen: $sub_pack")

        if (sub_pack != null){
            if (getPhoneNumSP.isNotEmpty()){
                localPaymentViewModel.fetchLocalPaymentData(getPhoneNumSP, sub_pack)
            }
            else{
                Toast.makeText(this, "Please Login First!", Toast.LENGTH_LONG).show()
            }
        }


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
                        //finish()
                        val intent = Intent(activity, MainActivity::class.java)
                        activity.startActivity(intent)
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
