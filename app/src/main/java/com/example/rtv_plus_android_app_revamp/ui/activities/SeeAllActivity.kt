package com.example.rtv_plus_android_app_revamp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val seeAllViewModel by viewModels<SeeAllViewModel>()
    var layoutManager = GridLayoutManager(this, 2)
    private var currentPage = 1
    private var isLoading = false
    private var isLastpage = false

    companion object {
        lateinit var catCode: String
        lateinit var catName: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolBarBackIconSubscribe.setOnClickListener {
            onBackPressed()
        }

        catCode = intent.getStringExtra("catcode").toString()
        catName = intent.getStringExtra("catname").toString()

        seeAllAdapter = SeeAllAdapter(emptyList())
        binding.rvSeeAll.layoutManager = layoutManager
        binding.rvSeeAll.adapter = seeAllAdapter

        if (catCode.isNotEmpty()) {
            loadMoreData() // Initial data load

            binding.rvSeeAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastpage) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0
                        ) {
                            isLoading = true
                            currentPage++
                            // End of the list reached, loading more data
                            loadMoreData()
                        }
                    }
                }
            })
        }
    }

    private fun loadMoreData() {
        seeAllViewModel.fetchSeeAllData(currentPage.toString(), catCode, "0", "1")

        lifecycleScope.launch {
            seeAllViewModel.seeAllData.collect {
                when (it) {
                    is ResultType.Loading -> {
                        binding.subscribeProgressBar.visibility = View.VISIBLE
                    }

                    is ResultType.Success -> {
                        val seeAllData = it.data

                        if (currentPage == 1) {
                            seeAllAdapter.seeAllData = seeAllData.contents
                            binding.tvSeeAllTitle.text = seeAllData.catname
                        } else {
                            if (!seeAllAdapter.seeAllData?.containsAll(seeAllData.contents)!!) {
                                seeAllAdapter.seeAllData =
                                    seeAllAdapter.seeAllData?.plus(seeAllData.contents)
                            }
                        }

                        binding.subscribeProgressBar.visibility = View.GONE
                        seeAllAdapter.notifyDataSetChanged()
                        isLoading = false

                        //checking last page
                        isLastpage = seeAllData.contents.isEmpty()
                    }

                    is ResultType.Error -> {
                        Toast.makeText(
                            this@SeeAllActivity,
                            "Something is wrong. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        isLoading = false
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
