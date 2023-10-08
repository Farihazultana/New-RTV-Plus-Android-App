package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.home.Data
import com.example.rtv_plus_android_app_revamp.ui.activities.LoginActivity
import com.example.rtv_plus_android_app_revamp.ui.activities.PlayerActivity
import com.example.rtv_plus_android_app_revamp.ui.activities.SeeAllActivity
import com.example.rtv_plus_android_app_revamp.utils.AppUtils.GoogleSignInKey
import com.example.rtv_plus_android_app_revamp.utils.AppUtils.LogInKey
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil
import com.jama.carouselview.CarouselView
import com.jama.carouselview.enums.IndicatorAnimationType
import com.jama.carouselview.enums.OffsetType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class ParentHomeAdapter(private var myContext: Context, var homeData: List<Data>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var job: Job? = null

    companion object {
        private const val TYPE_BANNER = 0
        private const val TYPE_CONTENT = 1
        private const val TYPE_THUMBNAIL = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_BANNER -> {
                val view = inflater.inflate(R.layout.row_obj_banner_carouselview, parent, false)
                BannerViewHolder(view)
            }

            TYPE_CONTENT -> {
                val view = inflater.inflate(R.layout.row_obj_parent_home_data, parent, false)
                ContentViewHolder(view)
            }

            TYPE_THUMBNAIL -> {
                val view = inflater.inflate(R.layout.row_obj_type_thumbnail, parent, false)
                ThumbnailViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = homeData[position]

        when (holder) {
            is ContentViewHolder -> {
                if (!currentItem.contents.isNullOrEmpty()) {
                    holder.childListAdapter =
                        ChildHomeAdapter(
                            myContext,
                            currentItem.contents,
                            currentItem.contentviewtype
                        )
                    holder.recyclerView.layoutManager = LinearLayoutManager(
                        holder.recyclerView.context, LinearLayoutManager.HORIZONTAL, false
                    )
                    holder.textView.text = currentItem.catname
                    holder.recyclerView.adapter = holder.childListAdapter
                    holder.seeAll.setOnClickListener {
                        val intent = Intent(holder.itemView.context, SeeAllActivity::class.java)
                        intent.putExtra("catcode", currentItem.catcode)
                        intent.putExtra("catname", currentItem.catname)
                        holder.itemView.context.startActivity(intent)
                    }
                } else {
                    holder.textView.visibility = View.GONE
                    holder.recyclerView.visibility = View.GONE
                    holder.seeAll.visibility = View.GONE
                }
            }

            is BannerViewHolder -> {
                holder.carouselView.apply {
                    size = homeData.size
                    resource = R.layout.row_obj_slider_view
                    autoPlay = true
                    indicatorAnimationType = IndicatorAnimationType.THIN_WORM
                    carouselOffset = OffsetType.CENTER
                    setCarouselViewListener { view, position ->
                        val imageView = view.findViewById<ImageView>(R.id.myimage)
                        Glide.with(imageView).load(currentItem.contents[position].image_location)
                            .placeholder(R.drawable.no_img).into(imageView)

                        imageView.setOnClickListener {
                            val spRes = SharedPreferencesUtil.getData(
                                myContext,
                                LogInKey,
                                ""
                            )
                            val spResGoogle = SharedPreferencesUtil.getData(
                                myContext,
                                GoogleSignInKey,
                                ""
                            )
                            Log.i("SPref", "onBindViewHolder: $spRes")
                            if (spRes.toString().isNotEmpty() || spResGoogle.toString()
                                    .isNotEmpty()
                            ) {
                                val intent =
                                    Intent(holder.itemView.context, PlayerActivity::class.java)
                                intent.putExtra("id", currentItem.contents[position].contentid)
                                intent.putExtra("type", "single")
                                holder.itemView.context.startActivity(intent)
                            } else {
                                val intent =
                                    Intent(holder.itemView.context, LoginActivity::class.java)
                                holder.itemView.context.startActivity(intent)
                            }
                        }
                    }
                    show()
                }
            }

            is ThumbnailViewHolder -> {

                holder.contentTitle.text = currentItem.catname

                if (!currentItem.contents.isNullOrEmpty()) {
                    val crossFadeFactory =
                        DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                    val customCrossFadeOptions =
                        DrawableTransitionOptions.withCrossFade(crossFadeFactory)
                    holder.thumbnailImage.setOnLongClickListener {
                        job?.cancel()
                        job = null
                        true
                    }
                    val seekbarMax = 100
                    holder.progressBar.max = seekbarMax

                    job = CoroutineScope(Dispatchers.Main).launch {
                        val delayDuration = 5000
                        val interval = 100
                        while (isActive) {
                            var randNum = Random.nextInt(1, currentItem.contents.size)
                            val imageUrl = currentItem.contents[randNum].image_location

                            holder.thumbnailImage.setOnClickListener {
                                val spRes = SharedPreferencesUtil.getData(
                                    myContext,
                                    LogInKey,
                                    ""
                                )
                                val spResGoogle = SharedPreferencesUtil.getData(
                                    myContext,
                                    GoogleSignInKey,
                                    ""
                                )
                                Log.i("SPref", "onBindViewHolder: $spRes")
                                if (spRes.toString().isNotEmpty() || spResGoogle.toString()
                                        .isNotEmpty()
                                ) {
                                    val intent =
                                        Intent(holder.itemView.context, PlayerActivity::class.java)
                                    intent.putExtra("id", currentItem.contents[randNum].contentid)
                                    holder.itemView.context.startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(holder.itemView.context, LoginActivity::class.java)
                                    holder.itemView.context.startActivity(intent)
                                }
                            }
                            for (i in 0 until (delayDuration / interval)) {
                                delay(interval.toLong())
                                holder.handler.post {
                                    // Update the SeekBar progress
                                    holder.progress =
                                        ((i + 1) * seekbarMax * interval) / delayDuration
                                    holder.progressBar.progress = holder.progress
                                }
                            }
                            Glide.with(holder.thumbnailImage.context).load(imageUrl)
                                .placeholder(R.drawable.no_img)
                                .transition(customCrossFadeOptions).into(holder.thumbnailImage)

                            holder.handler.post {
                                holder.progress = 0
                                holder.progressBar.progress = holder.progress
                            }
                        }
                    }

                } else {
                    holder.thumbnailImage.visibility = View.GONE
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return homeData.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentItem = homeData[position]
        return if (currentItem.contentviewtype == "4") {
            TYPE_BANNER
        } else if (currentItem.contentviewtype == "11") {
            TYPE_THUMBNAIL
        } else {
            TYPE_CONTENT
        }
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var childListAdapter: ChildHomeAdapter
        val recyclerView: RecyclerView = itemView.findViewById(R.id.listItem_recyclerview)
        val textView: TextView = itemView.findViewById(R.id.title_textviewID)
        val seeAll: TextView = itemView.findViewById(R.id.seeAll)
    }

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var bannerAdapter: BannerAdapter
        val carouselView: CarouselView = itemView.findViewById(R.id.carouselViewId)
    }

    inner class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnailImage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.seekBarId)
        val contentTitle: TextView = itemView.findViewById(R.id.contentTitle)
        val handler: Handler = Handler(Looper.getMainLooper())
        var progress = 0
    }

    fun cancelUpdates() {
        job?.cancel()
        job = null
    }
}