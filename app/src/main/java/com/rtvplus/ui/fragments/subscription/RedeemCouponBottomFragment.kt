package com.rtvplus.ui.fragments.subscription

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

        button.setOnClickListener {
            val otp: String = pinView.text.toString()
            redeemCouponViewModel.fetchRedeemCouponPaymentData(getPhoneNumSP, otp)
            lifecycleScope.launch {
                redeemCouponViewModel.redeemCuoponPaymentData.observe(this@RedeemCouponBottomFragment){
                    when(it){
                        is ResultType.Success -> {
                            val result = it.data
                            for (item in result){
                                val msg = item.error
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                                findNavController().navigate(R.id.SubscriptionFragment)
                                dismiss()
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