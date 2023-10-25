package com.rtvplus.ui.fragments.subscription

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.rtvplus.R
import com.rtvplus.databinding.FragmentSubscribeBottomBinding
import com.rtvplus.ui.activities.LocalPaymentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SubscribeBottomFragment : BottomSheetDialogFragment() {

    lateinit var bottomBinding: FragmentSubscribeBottomBinding
    private var isRedeemCouponBottomDialogOpened = false
    private val args = Bundle()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_subscribe_bottom, container, false)
        bottomBinding = FragmentSubscribeBottomBinding.bind(view)

        val packageText = arguments?.getString("packageText", "") ?: ""
        bottomBinding.tvPackage.text = packageText

        val sub_packLocalPayment = arguments?.getString("sub_pack", "") ?: ""
        Log.i("Payment", "selected package: $sub_packLocalPayment")

        val selectedPackforRedeemCoupon = arguments?.getString("pack_name", "") ?: ""
        Log.i("Redeem", "onCreateView: $selectedPackforRedeemCoupon")
        args.putString("redeem_pack", selectedPackforRedeemCoupon)

        bottomBinding.rbLocal.isChecked = true
        bottomBinding.cvLocalPayment.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.card_background_color
            )
        )

        bottomBinding.cvLocalPayment.setOnClickListener {
            bottomBinding.rbLocal.isChecked = true
            bottomBinding.rbRedeem.isChecked = false
            updateUI()

        }

        bottomBinding.cvRedeemCoupon.setOnClickListener {
            bottomBinding.rbRedeem.isChecked = true
            bottomBinding.rbLocal.isChecked = false
            updateUI()
        }

        bottomBinding.btnConfirmPayment.setOnClickListener {
            if (bottomBinding.rbRedeem.isChecked){
                val redeemCouponBottomFragment = RedeemCouponBottomFragment()
                redeemCouponBottomFragment.arguments = args
                redeemCouponBottomFragment.show(
                    childFragmentManager, redeemCouponBottomFragment.tag
                )
                isRedeemCouponBottomDialogOpened = true
            } else{
                val intent = Intent(requireContext(), LocalPaymentActivity::class.java)
                intent.putExtra("sub_pack", sub_packLocalPayment)
                //startActivity(intent)
                startActivityForResult(intent,1234)
                dismiss()
            }
        }

        if (isRedeemCouponBottomDialogOpened){
            dismiss()
            findNavController().popBackStack()
        }

        return view
    }

    private fun updateUI() {
        val localCardView = bottomBinding.cvLocalPayment
        val redeemCardView = bottomBinding.cvRedeemCoupon

        if (bottomBinding.rbLocal.isChecked) {
            localCardView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.card_background_color
                )
            )
            redeemCardView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        } else if (bottomBinding.rbRedeem.isChecked) {
            redeemCardView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.card_background_color
                )
            )
            localCardView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        }
    }

}