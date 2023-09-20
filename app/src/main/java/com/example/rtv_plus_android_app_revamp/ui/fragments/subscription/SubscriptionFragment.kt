package com.example.rtv_plus_android_app_revamp.ui.fragments.subscription

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubschemesItem
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscribeBottomBinding
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscriptionBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.SubscriptionAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.ViewModels
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubscriptionFragment : Fragment(), SubscriptionAdapter.CardClickListener {
    private lateinit var binding: FragmentSubscriptionBinding
    private lateinit var bottomBinding: FragmentSubscribeBottomBinding
    val bottomSheetFragment = SubscribeBottomFragment()
    val args = Bundle()
    private lateinit var subscriptionAdapter: SubscriptionAdapter
    private val subscriptionViewModel by viewModels<ViewModels>()

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


//        binding.btWeeklySubscription.setOnClickListener {
//            showBottomSheet("TK 15 for 7 days")
//        }
//        binding.btMonthlySubscription.setOnClickListener {
//            showBottomSheet("TK 50 for 1 Month")
//        }
//        binding.btSixMonthSubscription.setOnClickListener {
//            showBottomSheet("TK 275 for 6 Months")
//        }
//        binding.btYearlySubscription.setOnClickListener {
//            showBottomSheet("TK 500 for 1 Year")
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscriptionViewModel.fetchSubscriptionData("8801825414747")

        subscriptionAdapter = SubscriptionAdapter(emptyList(), this)
        binding.rvSubscriptionPacks.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSubscriptionPacks.adapter = subscriptionAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            subscriptionViewModel.subscriptionData.collect { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.subscribeProgressBar.visibility = View.VISIBLE
                        binding.textView.visibility = View.GONE

                    }

                    is ResultType.Success -> {
                        val subscriptionData = result.data
                        subscriptionAdapter.subscriptionData = subscriptionData.subschemes
                        binding.subscribeProgressBar.visibility = View.GONE
                        binding.textView.visibility = View.VISIBLE
                        subscriptionAdapter.notifyDataSetChanged()
                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Something is wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        //btn click listener
        binding.btnConfirmPayment.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        //val bottomSheetFragment = SubscribeBottomFragment()

        //val args = Bundle()
        //args.putString("packageText", packageText)
        bottomSheetFragment.arguments = args

        // Pass the bottomBinding to the bottomSheetFragment
        bottomSheetFragment.bottomBinding = bottomBinding

        bottomSheetFragment.show(
            childFragmentManager, bottomSheetFragment.tag
        )
    }

    private fun showPackage(packageText: String?){

    }

    override fun onCardClickListener(item: SubschemesItem?) {
        //showBottomSheet(item?.sub_text)
        //val args = Bundle()
        args.putString("packageText", item?.sub_text)
    }


}
