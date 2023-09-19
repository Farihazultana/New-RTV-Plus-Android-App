package com.example.rtv_plus_android_app_revamp.ui.fragments.subscription

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscribeBottomBinding
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscriptionBinding

class SubscriptionFragment : Fragment() {
    private lateinit var binding: FragmentSubscriptionBinding
    private lateinit var bottomBinding: FragmentSubscribeBottomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        bottomBinding = FragmentSubscribeBottomBinding.inflate(inflater, container, false)


        //go to previous screen
        val toolBarIconSubscribe = binding.toolBarIconSubscribe
        toolBarIconSubscribe.setOnClickListener {
            val navController = findNavController(binding.root)
            navController.navigate(R.id.HomeFragment)
        }


        binding.btWeeklySubscription.setOnClickListener {
            showBottomSheet("TK 15 for 7 days")
        }
        binding.btMonthlySubscription.setOnClickListener {
            showBottomSheet("TK 50 for 1 Month")
        }
        binding.btSixMonthSubscription.setOnClickListener {
            showBottomSheet("TK 275 for 6 Months")
        }
        binding.btYearlySubscription.setOnClickListener {
            showBottomSheet("TK 500 for 1 Year")
        }

        return binding.root
    }

    private fun showBottomSheet(packageText: String) {
        val bottomSheetFragment = SubscribeBottomFragment()

        val args = Bundle()
        args.putString("packageText", packageText)
        bottomSheetFragment.arguments = args

        // Pass the bottomBinding to the bottomSheetFragment
        bottomSheetFragment.bottomBinding = bottomBinding

        bottomSheetFragment.show(
            childFragmentManager, bottomSheetFragment.tag
        )
    }


}
