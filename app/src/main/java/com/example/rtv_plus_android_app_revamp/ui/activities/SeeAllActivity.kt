package com.example.rtv_plus_android_app_revamp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rtv_plus_android_app_revamp.databinding.ActivitySeeAllBinding
import com.example.rtv_plus_android_app_revamp.ui.adapters.SeeAllAdapter
import com.example.rtv_plus_android_app_revamp.ui.viewmodels.SeeAllViewModel
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeeAllActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeAllBinding
    private lateinit var seeAllAdapter: SeeAllAdapter

    private val seeAllViewModels by viewModels<SeeAllViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolBarBackIconSubscribe.setOnClickListener {
            onBackPressed()
        }

        val catCode = intent.getStringExtra("catcode")
        val catName = intent.getStringExtra("catname")

        seeAllViewModels.fetchSeeAllData("1", catCode.toString(), "0", "1")

        seeAllAdapter = SeeAllAdapter(emptyList())
        binding.rvSeeAll.layoutManager = GridLayoutManager(this, 2)
        binding.rvSeeAll.adapter = seeAllAdapter

        if (catCode != null) {
            lifecycleScope.launch {
                seeAllViewModels.seeAllData.collect {
                    when (it) {
                        is ResultType.Loading -> {
                            binding.subscribeProgressBar.visibility = View.VISIBLE
                        }

                        is ResultType.Success -> {
                            val seeAllData = it.data
                            seeAllAdapter.seeAllData = seeAllData.contents
                            binding.tvSeeAllTitle.text = catName
                            binding.subscribeProgressBar.visibility = View.GONE
                            seeAllAdapter.notifyDataSetChanged()
                        }

                        is ResultType.Error -> {
                            Toast.makeText(
                                this@SeeAllActivity,
                                "Something is wrong. Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val backStackEntryCount = fragmentManager.backStackEntryCount

        if (backStackEntryCount > 0) {
            for (i in backStackEntryCount - 1 downTo 0) {
                val entry = fragmentManager.getBackStackEntryAt(i)
                if (entry.name == "HomeFragment") {
                    fragmentManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    return
                }
            }
        }
        super.onBackPressed()
    }
}