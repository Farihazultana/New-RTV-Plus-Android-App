package com.rtvplus.ui.fragments.subscription

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.rtvplus.R
import com.rtvplus.data.models.subscription.SubschemesItem
import com.rtvplus.databinding.FragmentSubscribeBottomBinding
import com.rtvplus.databinding.FragmentSubscriptionBinding
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.ui.adapters.SubscriptionAdapter
import com.rtvplus.ui.viewmodels.LogInViewModel
import com.rtvplus.ui.viewmodels.SubscriptionViewModel
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.ResultType
import com.rtvplus.utils.SharedPreferencesUtil
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
    //private val logInViewModel by viewModels<LogInViewModel>()
    private var selectedPositions = -1

    private var getPhoneNumSP: String = ""
    private var sub_pack: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        bottomBinding = FragmentSubscribeBottomBinding.inflate(inflater, container, false)


        val fragmentManager = requireActivity().supportFragmentManager
        val toolBarIconSubscribe = binding.toolBarIconSubscribe

        getPhoneNumSP = SharedPreferencesUtil.getData(
            requireContext(),
            UsernameInputKey,
            ""
        ).toString()

        //for toolbar back press
        toolBarIconSubscribe.setOnClickListener {
            fragmentManager.popBackStack()
        }

        //go to previous screen
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                fragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        subscriptionAdapter = SubscriptionAdapter(this)
        binding.rvSubscriptionPacks.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSubscriptionPacks.adapter = subscriptionAdapter


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPhoneNumSP = SharedPreferencesUtil.getData(
            requireContext(),
            UsernameInputKey,
            ""
        ).toString()

        //btn click listener
        binding.btnContinuePayment.setOnClickListener {
            if (selectedPositions != -1) {
                if (getPhoneNumSP.isNotEmpty()) {
                    binding.btnContinuePayment.setBackgroundColor(resources.getColor(R.color.grey))
                    selectedPositions = -1
                    showBottomSheet()

                } else {
                    Toast.makeText(requireContext(), "Please Login First!", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivityForResult(intent, 1234)
                }

            } else {
                Toast.makeText(
                    requireActivity(),
                    "PLease select your plan first",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        observeSubscription()

    }

    override fun onResume() {
        //loginApi call
        //logInViewModel.fetchLogInData("8801954953031", "115030", "no", "1")

        subscription()

        super.onResume()
    }

    private fun subscription() {
        subscriptionViewModel.fetchSubscriptionData(getPhoneNumSP)
    }

    private fun observeSubscription() {
        viewLifecycleOwner.lifecycleScope.launch {
            subscriptionViewModel.subscriptionData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultType.Loading -> {
                        binding.subscribeProgressBar.visibility = View.VISIBLE
                        binding.textView.visibility = View.GONE
                        binding.btnContinuePayment.visibility = View.GONE
                    }

                    is ResultType.Success -> {

                        /*binding.rvSubscriptionPacks.layoutManager =
                            GridLayoutManager(requireContext(), 2)
                        binding.rvSubscriptionPacks.adapter = subscriptionAdapter*/

                        val subscriptionData = result.data
                        subscriptionAdapter.setData(subscriptionData.subschemes)
                        binding.subscribeProgressBar.visibility = View.GONE
                        binding.textView.visibility = View.VISIBLE
                        //binding.textView.text = LoginActivity.packText
                        binding.btnContinuePayment.visibility = View.VISIBLE

                        for (item in subscriptionData.subschemes) {
                            if (item.userpack != "nopack") {
                                binding.btnContinuePayment.visibility = View.GONE
                                binding.textView.text = item.packtext
                                sub_pack = item.sub_pack
                                Log.i(
                                    "Subscription",
                                    "onViewCreated: ${item.packtext} & ${item.userpack}"
                                )

                            } else {
                                binding.textView.text = item.packtext
                                binding.btnContinuePayment.visibility = View.VISIBLE
                            }
                        }
                         subscriptionAdapter.notifyDataSetChanged()//------***
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            subscription()

            //subscriptionAdapter.notifyDataSetChanged()
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
        if (item?.userpack == "nopack") {
            this.selectedPositions = position
            //binding.btnContinuePayment.isEnabled = selectedPositions != -1
            //showBottomSheet(item?.sub_text)
            args.putString("packageText", item.sub_text)
            args.putString("sub_pack", item.sub_pack)
            args.putString("pack_name", item.pack_name)
            if (selectedPositions != -1) {
                binding.btnContinuePayment.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
            } else {
                binding.btnContinuePayment.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey
                    )
                )
            }
        } else {
            this.selectedPositions = -1
        }
        subscriptionAdapter.notifyDataSetChanged()
    }

}