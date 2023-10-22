package com.rtvplus.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.rtvplus.R
import com.rtvplus.data.models.home.Data
import com.rtvplus.databinding.ItemCustomFixedSizeLayout3Binding
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.ui.activities.PlayerActivity
import com.rtvplus.ui.activities.SeeAllActivity
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator2
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.imaginativeworld.whynotimagecarousel.utils.setImage
import kotlin.random.Random

class ParentHomeAdapter(
    private var myContext: Context,
    var homeData: List<Data>,
    private val navController: NavController,
    var isPemiumUser: Int?,
    var lifecycle: Lifecycle
) :
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
                if (currentItem.contents.isNotEmpty()) {
                    holder.childListAdapter =
                        ChildHomeAdapter(
                            myContext,
                            currentItem.contents,
                            currentItem.contentviewtype,
                            navController,
                            isPemiumUser
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

                holder.carousel4.registerLifecycle(lifecycle)
                holder.carousel4.infiniteCarousel = false
                holder.carousel4.infiniteCarousel = false
                holder.carousel4.autoPlay = true

                // Custom view
                holder.carousel4.carouselListener = object : CarouselListener {
                    override fun onCreateViewHolder(
                        layoutInflater: LayoutInflater,
                        parent: ViewGroup
                    ): ViewBinding {
                        return ItemCustomFixedSizeLayout3Binding.inflate(
                            layoutInflater,
                            parent,
                            false
                        )
                    }

                    override fun onBindViewHolder(
                        binding: ViewBinding,
                        item: CarouselItem,
                        position: Int
                    ) {
                        val currentBinding = binding as ItemCustomFixedSizeLayout3Binding

                        currentBinding.imageView.apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP

                            setImage(item, R.drawable.ic_wb_cloudy_with_padding)
                        }
                    }
                }



//                holder.carousel4.carouselListener = object : CarouselListener {
//                    override fun onCreateViewHolder(
//                        layoutInflater: LayoutInflater,
//                        parent: ViewGroup
//                    ): ViewBinding {
//                        return ItemCustomFixedSizeLayout3Binding.inflate(
//                            layoutInflater,
//                            parent,
//                            false
//                        )
//                    }
//
//                    override fun onBindViewHolder(
//                        binding: ViewBinding,
//                        item: CarouselItem,
//                        position: Int
//                    ) {
//                        val currentBinding = binding as ItemCustomFixedSizeLayout3Binding
//
//                        currentBinding.imageView.apply {
//                            scaleType = ImageView.ScaleType.CENTER_CROP
//
//                            setImage(item, R.drawable.ic_wb_cloudy_with_padding)
//                        }
//                    }
//                }


                val listFour = mutableListOf<CarouselItem>()

                for (item in currentItem.contents) {
                    listFour.add(
                        CarouselItem(
                            imageUrl = item.image_location
                        )
                    )
                }

                holder.carousel4.setData(listFour)
                holder.carousel4.setIndicator(holder.custom_indicator)



//                holder.carouselView.apply {
//                    size = homeData.size
//                    resource = R.layout.row_obj_slider_view
//                    autoPlay = true
//                    indicatorAnimationType = IndicatorAnimationType.THIN_WORM
//                    carouselOffset = OffsetType.CENTER
//                    setCarouselViewListener { view, position ->
//                        val imageView = view.findViewById<ImageView>(R.id.myimage)
//                        Glide.with(imageView).load(currentItem.contents[position].image_location)
//                            .placeholder(R.drawable.no_img).into(imageView)
//
//                        imageView.setOnClickListener {
//
//                            val username = SharedPreferencesUtil.getData(
//                                myContext,
//                                UsernameInputKey,
//                                ""
//                            )
//                            if (username.toString().isNotEmpty()) {
//                                val intent =
//                                    Intent(holder.itemView.context, PlayerActivity::class.java)
//                                intent.putExtra("id", currentItem.contents[position].contentid)
//                                intent.putExtra("type", "single")
//                                holder.itemView.context.startActivity(intent)
//                            } else {
//                                val intent =
//                                    Intent(holder.itemView.context, LoginActivity::class.java)
//                                holder.itemView.context.startActivity(intent)
//                            }
//                        }
//                    }
//                    show()
//                }
            }

            is ThumbnailViewHolder -> {

                holder.contentTitle.text = currentItem.catname

                if (currentItem.contents.isNotEmpty()) {
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
                            val randNum = Random.nextInt(1, currentItem.contents.size)
                            val imageUrl = currentItem.contents[randNum].image_location
                            holder.thumbnailImage.setOnClickListener {
                                val username = SharedPreferencesUtil.getData(
                                    myContext,
                                    UsernameInputKey,
                                    ""
                                )
                                if (username.toString().isNotEmpty()) {
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
        val carousel4: ImageCarousel = itemView.findViewById(R.id.carousel4)
        val custom_indicator : CircleIndicator2 = itemView.findViewById(R.id.custom_indicator)
        //  val carouselView : CarouselView = itemView.findViewById(R.id.carouselViewId)
    }

    inner class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnailImage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.seekBarId)
        val contentTitle: TextView = itemView.findViewById(R.id.contentTitle)
        val handler: Handler = Handler(Looper.getMainLooper())
        var progress = 0
    }

}