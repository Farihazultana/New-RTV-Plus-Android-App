package com.rtvplus.ui.activities

import com.rtvplus.utils.LogInUtil
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
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
    private val saveLocalPaymentViewModel by viewModels<SaveLocalPaymentViewModel>()
    private lateinit var localPaymentView: WebView

    var getPhoneNumSP: String = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(view)

        localPaymentView = binding.wvLocalPayment
        localPaymentView.settings.javaScriptEnabled = true
        localPaymentView.webViewClient =
            LocalPaymentWebViewClient(
                this,
                saveLocalPaymentViewModel
            )

        getPhoneNumSP= SharedPreferencesUtil.getData(
        this,
        UsernameInputKey,
        "defaultValue"
        ).toString()
        val sub_pack = intent.getStringExtra("sub_pack")

        localPaymentData(sub_pack)
    }

    private fun localPaymentData(sub_pack: String?) {
        if (sub_pack != null) {
            if (getPhoneNumSP.isNotEmpty()) {
                localPaymentViewModel.fetchLocalPaymentData(getPhoneNumSP, sub_pack)
            } else {
                Toast.makeText(this, "Please Login First!", Toast.LENGTH_LONG).show()
            }
        }

        lifecycleScope.launch {
            localPaymentViewModel.localPaymentData.observe(this@LocalPaymentActivity) {
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

                    else -> {
                        Toast.makeText(
                            this@LocalPaymentActivity,
                            "Something went wrong!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                    if (url == "https://rtvplus.tv/") {
                        setResult(Activity.RESULT_OK)
                        finish()
                        return true
                    }

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
                        if (paymentId.isNotEmpty() && orderId.isNotEmpty()) {
                            handleSavedLocalPaymentData(paymentId, orderId)
                            setResult(Activity.RESULT_OK)
                        } else {
                            setResult(Activity.RESULT_OK)
                            finish() //if user cancels payment LocalPayment activity will be gone
                        }
                        setResult(Activity.RESULT_OK)
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
                            setResult(Activity.RESULT_OK)
                            if(it.data.result.isNotEmpty()){
                                finish() //if user completes payment LocalPayment activity will be gone
                            }

                        }

                        is ResultType.Error -> {
                            Toast.makeText(
                                this@LocalPaymentActivity,
                                "Something wend wrong, please try again!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            /*Toast.makeText(
                                this@LocalPaymentActivity,
                                "Something wend wrong, please try again!",
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }
                    }
                }
            }
        }
    }


    override fun finish() {
        binding.wvLocalPayment.removeAllViews()
        if (localPaymentView.isNotEmpty()) {
            localPaymentView.clearHistory()
            localPaymentView.clearCache(true)
            localPaymentView.clearView()
            localPaymentView.destroy()
            //localPaymentView = null
        }

        super.finish()
    }

}
