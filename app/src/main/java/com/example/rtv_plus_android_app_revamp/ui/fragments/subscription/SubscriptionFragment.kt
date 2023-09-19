package com.example.rtv_plus_android_app_revamp.ui.fragments.subscription

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubschemesItem
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscribeBottomBinding
import com.example.rtv_plus_android_app_revamp.databinding.FragmentSubscriptionBinding
import com.example.rtv_plus_android_app_revamp.databinding.SubscriptionItemBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.SubscriptionAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SubscriptionViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SubscriptionFragment : Fragment(), SubscriptionAdapter.CardClickListener {
    private lateinit var binding: FragmentSubscriptionBinding
    private lateinit var bottomBinding: FragmentSubscribeBottomBinding
    private lateinit var subscriptionItemBinding: SubscriptionItemBinding
    private lateinit var subscriptionAdapter: SubscriptionAdapter
    private val subscriptionViewModel by viewModels<SubscriptionViewModel>()

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

        subscriptionViewModel.fetchSubscriptionData("8801748855192")

        subscriptionAdapter = SubscriptionAdapter(emptyList(), this)
        binding.rvSubscriptionPacks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubscriptionPacks.adapter = subscriptionAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            subscriptionViewModel.subscriptionData.observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.subscribeProgressBar.visibility = View.VISIBLE
                        binding.textView.visibility = View.GONE
                        binding.rvSubscriptionPacks.visibility = View.GONE
                    }

                    is ResultType.Success -> {
                        val subscriptionData = result.data
                        subscriptionAdapter.subscriptionData = subscriptionData.subschemes
                        binding.subscribeProgressBar.visibility = View.GONE
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
            })
        }

    }

    private fun showBottomSheet(packageText: String?) {
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

    override fun onCardClickListener(item: SubschemesItem?) {
        showBottomSheet(item?.packName)
    }


}
