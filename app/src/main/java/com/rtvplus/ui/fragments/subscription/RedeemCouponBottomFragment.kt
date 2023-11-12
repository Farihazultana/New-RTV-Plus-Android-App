package com.rtvplus.ui.fragments.subscription

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rtvplus.R
import com.rtvplus.databinding.FragmentRedeemCouponBottomBinding
import com.rtvplus.ui.viewmodels.RedeemCouponViewModel
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rtvplus.utils.AppUtils.UsernameInputKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RedeemCouponBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRedeemCouponBottomBinding
    private val redeemCouponViewModel by viewModels<RedeemCouponViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRedeemCouponBottomBinding.inflate(inflater, container, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val view = binding.root

        val pinView = binding.pinview
        val button = binding.showOtp

        val redeemText = arguments?.getString("redeem_pack", "") ?: ""
        binding.textView3.text = "Enter $redeemText redeem cuopon"

        val getPhoneNumSP = SharedPreferencesUtil.getData(
            requireContext(),
           UsernameInputKey,
            ""
        ).toString()

        observe()

        button.setOnClickListener {
            val otp: String = pinView.text.toString()
            if(otp.length==8){
                redeemCouponViewModel.fetchRedeemCouponPaymentData(getPhoneNumSP, otp)
            }else if (otp.length <8 || otp.length >8){
                Toast.makeText(requireActivity(), "Please enter a valid a valid coupon code to proceed", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun observe() {
        lifecycleScope.launch {
            redeemCouponViewModel.redeemCuoponPaymentData.observe(this@RedeemCouponBottomFragment) {
                when (it) {
                    is ResultType.Success -> {
                        val result = it.data.response
                        Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.SubscriptionFragment)
                        dismiss()
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

}