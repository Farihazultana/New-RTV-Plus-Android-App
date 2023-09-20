package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.home.Data
import com.jama.carouselview.CarouselView
import com.jama.carouselview.enums.IndicatorAnimationType
import com.jama.carouselview.enums.OffsetType
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.util.TimerTask
import kotlin.random.Random

class ParentHomeAdapter(var homeData: List<Data>) :
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
                val view = inflater.inflate(R.layout.row_obj_banner_viewpager, parent, false)
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
                        ChildHomeAdapter(currentItem.contents, currentItem.contentviewtype)
                    holder.recyclerView.layoutManager = LinearLayoutManager(
                        holder.recyclerView.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    holder.textView.text = currentItem.catname
                    holder.recyclerView.adapter = holder.childListAdapter
                } else {
                    holder.textView.visibility = View.GONE
                    holder.recyclerView.visibility = View.GONE
                    holder.seeAll.visibility = View.GONE
                }
            }

            is BannerViewHolder -> {


                 val bannerImages = listOf(
                    "https://example.com/image1.jpg",
                    "https://example.com/image2.jpg",
                    "https://example.com/image3.jpg"
                )


                holder.carouselView.apply {
                    size = homeData.size
                    resource = R.layout.row_obj_slider_view
                    autoPlay = true
                    indicatorAnimationType = IndicatorAnimationType.THIN_WORM
                    carouselOffset = OffsetType.CENTER
                    setCarouselViewListener { view, position ->
                        // Example here is setting up a full image carousel
                        val imageView = view.findViewById<ImageView>(R.id.myimage)
                      //  imageView.setImageURI(currentItem.contents[position].image_location.toUri())

                        Glide.with(imageView)
                            .load(currentItem.contents[position].image_location)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imageView)



                        //imageView.setImageDrawable(resources.getDrawable(currentItem.contents[position].image_location)
                    }
                    // After you finish setting up, show the CarouselView
                    show()
                }
            }




//                val bannerAdapter = BannerAdapter(holder.itemView.context, bannerImages)
//                holder.viewPager.adapter = bannerAdapter





//                holder.sliderAdapter = SliderAdapter(currentItem.contents)
//                holder.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
//                holder.sliderView.setSliderAdapter(holder.sliderAdapter)
//                holder.sliderView.scrollTimeInSec = 3
//                holder.sliderView.isAutoCycle = true
//                holder.sliderView.startAutoCycle()

//                holder.insicatorAdapter =
//                    IndicatorAdapter(currentItem.contents)
//                holder.indicatorRecyclerview.layoutManager = LinearLayoutManager(
//                    holder.indicatorRecyclerview.context,
//                    LinearLayoutManager.HORIZONTAL,
//                    false
//                )
//                holder.indicatorRecyclerview.adapter = holder.insicatorAdapter




            is ThumbnailViewHolder -> {

                if (!currentItem.contents.isNullOrEmpty()) {

                    val crossFadeFactory = DrawableCrossFadeFactory.Builder()
                        .setCrossFadeEnabled(true)
                        .build()

                    holder.thumbnailImage.setOnLongClickListener {
                        job?.cancel()
                        job = null
                        true
                    }

                    if (job == null) {
                        job = CoroutineScope(Dispatchers.Main).launch {
                            while (isActive) {

                                var randNum = Random.nextInt(1, currentItem.contents.size)

                                val onTouchListener = View.OnTouchListener { _, event ->
                                    when (event.action) {
                                        MotionEvent.ACTION_DOWN -> {

                                            true // Return true to consume the event
                                        }

                                        MotionEvent.ACTION_UP -> {
                                        randNum = Random.nextInt(1, currentItem.contents.size)

                                            true // Return true to consume the event
                                        }

                                        else -> false
                                    }
                                }

                                holder.thumbnailImage.setOnTouchListener(onTouchListener)

                                val imageUrl = currentItem.contents[randNum].image_location

                                Glide.with(holder.thumbnailImage.context)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .transition(
                                        DrawableTransitionOptions.withCrossFade(
                                            crossFadeFactory
                                        )
                                    )
                                    .into(holder.thumbnailImage)

                                delay(1000)
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
        } else if (currentItem.contentviewtype == "1") {
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
        val carouselView : CarouselView = itemView.findViewById(R.id.carouselViewId)

    }

    inner class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnailImage)

    }

    fun cancelUpdates() {
        job?.cancel()
        job = null
    }






}