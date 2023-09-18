package com.example.rtv_plus_android_app_revamp.ui.fregments.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscribeBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SubscribeBottomFragment : BottomSheetDialogFragment() {

    lateinit var bottomBinding: FragmentSubscribeBottomBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_subscribe_bottom, container, false)
        bottomBinding = FragmentSubscribeBottomBinding.bind(view)

        val packageText = arguments?.getString("packageText", "") ?: ""
        bottomBinding.tvPackage.text = packageText

        val rbLocal = bottomBinding.rbLocal
        val rbRedeem = bottomBinding.rbRedeem

        bottomBinding.cvLocalPayment.setOnClickListener {
            rbLocal.isChecked = true
            rbRedeem.isChecked = false
            bottomBinding.cvLocalPayment.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_background_color))
            bottomBinding.cvRedeemCoupon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        bottomBinding.cvRedeemCoupon.setOnClickListener {
            rbRedeem.isChecked = true
            rbLocal.isChecked = false
            bottomBinding.cvRedeemCoupon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_background_color))
            bottomBinding.cvLocalPayment.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        return view
    }



}