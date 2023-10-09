package com.example.rtv_plus_android_app_revamp.ui.fragments.subscription

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscribeBottomBinding
import com.example.rtv_plus_android_app_revamp.ui.activities.LocalPaymentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SubscribeBottomFragment : BottomSheetDialogFragment() {

    lateinit var bottomBinding: FragmentSubscribeBottomBinding
    private var isLocalSelected = false
    private var isRedeemSelected = false
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

        val rbLocal = bottomBinding.rbLocal
        val rbRedeem = bottomBinding.rbRedeem

        rbLocal.setOnClickListener {
            isLocalSelected = true
            isRedeemSelected = false
            updateUI()
        }

        rbRedeem.setOnClickListener {
            isLocalSelected = false
            isRedeemSelected = true
            updateUI()
        }

        bottomBinding.cvLocalPayment.setOnClickListener {
            isLocalSelected = true
            isRedeemSelected = false
            updateUI()
            bottomBinding.btnConfirmPayment.setOnClickListener {
                isLocalSelected = false
                val intent = Intent(requireContext(), LocalPaymentActivity::class.java)
                intent.putExtra("sub_pack", sub_packLocalPayment)
                startActivity(intent)
            }
        }

        bottomBinding.cvRedeemCoupon.setOnClickListener {
            isLocalSelected = false
            isRedeemSelected = true
            updateUI()
            bottomBinding.btnConfirmPayment.setOnClickListener {
                val redeemCouponBottomFragment = RedeemCouponBottomFragment()

                redeemCouponBottomFragment.show(
                    childFragmentManager, redeemCouponBottomFragment.tag
                )
            }
        }


        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        bottomBinding.rbLocal.isChecked = false
        bottomBinding.rbRedeem.isChecked = false
    }

    private fun updateUI() {
        val localCardView = bottomBinding.cvLocalPayment
        val redeemCardView = bottomBinding.cvRedeemCoupon

        if (isLocalSelected) {
            bottomBinding.rbLocal.isChecked = true
            bottomBinding.rbRedeem.isChecked = false
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
        } else if (isRedeemSelected) {
            bottomBinding.rbLocal.isChecked = false
            bottomBinding.rbRedeem.isChecked = true
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
        } else {
            // Neither is selected, you can update the UI accordingly if needed
            bottomBinding.rbLocal.isChecked = false
            bottomBinding.rbRedeem.isChecked = false
            localCardView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            redeemCardView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        }
    }

}