package com.rtvplus.ui.fragments.subscription

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.rtvplus.R
import com.rtvplus.data.models.subscription.SubschemesItem
import com.rtvplus.databinding.FragmentSubscribeBottomBinding
import com.rtvplus.databinding.FragmentSubscriptionBinding
import com.rtvplus.ui.adapters.SubscriptionAdapter
import com.rtvplus.ui.viewmodels.SubscriptionViewModel
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.ui.activities.MainActivity
import com.rtvplus.utils.AppUtils.UsernameInputKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubscriptionFragment : Fragment(), SubscriptionAdapter.CardClickListener {
    private lateinit var binding: FragmentSubscriptionBinding
    private lateinit var bottomBinding: FragmentSubscribeBottomBinding
    private val bottomSheetFragment = SubscribeBottomFragment()
    private val args = Bundle()
    private lateinit var subscriptionAdapter: SubscriptionAdapter
    private val subscriptionViewModel by viewModels<SubscriptionViewModel>()
    private var selectedPositions = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        bottomBinding = FragmentSubscribeBottomBinding.inflate(inflater, container, false)


        val fragmentManager = requireActivity().supportFragmentManager
        val toolBarIconSubscribe = binding.toolBarIconSubscribe

        toolBarIconSubscribe.setOnClickListener {
            fragmentManager.popBackStack()
//            val navController = findNavController(binding.root)
//            navController.navigate(R.id.HomeFragment)
//            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
//            bottomNavigationView.selectedItemId = R.id.HomeFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentManager = requireActivity().supportFragmentManager
        //go to previous screen
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                fragmentManager.popBackStack()

//                val navController = findNavController(binding.root)
//                navController.navigate(R.id.HomeFragment)
//                val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBarId)
//                bottomNavigationView.selectedItemId = R.id.HomeFragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val getPhoneNumSP = SharedPreferencesUtil.getData(
            requireContext(),
            UsernameInputKey,
            ""
        ).toString()
        if (getPhoneNumSP.isNotEmpty()){
            subscriptionViewModel.fetchSubscriptionData(getPhoneNumSP)
        }
        else{
            Toast.makeText(requireContext(), "Please Login First!", Toast.LENGTH_LONG).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }


        subscriptionAdapter = SubscriptionAdapter(emptyList(), this)
        binding.rvSubscriptionPacks.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSubscriptionPacks.adapter = subscriptionAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            subscriptionViewModel.subscriptionData.collect { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.subscribeProgressBar.visibility = View.VISIBLE
                        binding.textView.visibility = View.GONE
                        binding.btnContinuePayment.visibility = View.GONE
                    }

                    is ResultType.Success -> {
                        val subscriptionData = result.data
                        subscriptionAdapter.subscriptionData = subscriptionData.subschemes
                        binding.subscribeProgressBar.visibility = View.GONE
                        binding.textView.visibility = View.VISIBLE
                        binding.btnContinuePayment.visibility = View.VISIBLE
                        for(item in subscriptionData.subschemes){
                            if(item.userpack != "nopack"){
                                binding.btnContinuePayment.visibility = View.GONE
                                binding.textView.text = item.packtext
                                Log.i("Subscription", "onViewCreated: ${item.packtext} & ${item.userpack}")
                            }
                        }
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
        binding.btnContinuePayment.setOnClickListener {
            if (selectedPositions != -1) {
                showBottomSheet()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "PLease select your plan first",
                    Toast.LENGTH_LONG
                ).show()
            }

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

    override fun onCardClickListener(position: Int, item: SubschemesItem?) {
        if(item?.userpack == "nopack"){
            this.selectedPositions = position
            //binding.btnContinuePayment.isEnabled = selectedPositions != -1
            //showBottomSheet(item?.sub_text)
            args.putString("packageText", item.sub_text)
            args.putString("sub_pack", item.sub_pack)
            args.putString("pack_name", item.pack_name)
            if (selectedPositions != -1) {
                binding.btnContinuePayment.setBackgroundColor(resources.getColor(R.color.green))
            } else {
                binding.btnContinuePayment.setBackgroundColor(resources.getColor(R.color.grey))
            }
        }else{
            this.selectedPositions = position
        }
    }

}