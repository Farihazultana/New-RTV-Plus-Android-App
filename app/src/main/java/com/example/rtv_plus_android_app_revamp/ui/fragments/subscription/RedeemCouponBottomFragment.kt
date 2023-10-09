package com.example.rtv_plus_android_app_revamp.ui.fragments.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.rtv_plus_android_app_revamp.databinding.FragmentRedeemCouponBottomBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.LocalPaymentActivity
import com.example.rtv_plus_android_app_revamp.ui.activities.LoginActivity
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.RedeemCouponViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RedeemCouponBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRedeemCouponBottomBinding
    private val redeemCouponViewModel by viewModels<RedeemCouponViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRedeemCouponBottomBinding.inflate(inflater, container, false)
        val view = binding.root

        val pinView = binding.pinview
        val button = binding.showOtp

        val getPhoneNumSP = SharedPreferencesUtil.getData(
            requireContext(),
            LoginActivity.UsernameInputKey,
            "defaultValue"
        ).toString()

        button.setOnClickListener {
            val otp: String = pinView.text.toString()
            redeemCouponViewModel.fetchRedeemCouponPaymentData(getPhoneNumSP, otp)
            lifecycleScope.launch {
                redeemCouponViewModel.redeemCuoponPaymentData.observe(this@RedeemCouponBottomFragment){
                    when(it){
                        is ResultType.Loading -> {

                        }
                        is ResultType.Success -> {
                            val result = it.data
                            for (item in result){
                                item.error
                                Toast.makeText(requireContext(), item.error, Toast.LENGTH_LONG).show()
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

}