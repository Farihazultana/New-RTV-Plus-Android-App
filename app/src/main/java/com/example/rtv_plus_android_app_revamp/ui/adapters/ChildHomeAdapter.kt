package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.home.Content
import com.example.rtv_plus_android_app_revamp.ui.activities.PlayerActivity

class ChildHomeAdapter(
    private var contentData: List<Content>,
    private var contentViewType: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_BANNER = 0
        private const val TYPE_CONTENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            TYPE_BANNER -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.row_obj_banner, parent, false)
//                BannerViewHolder(view)
//            }
            TYPE_CONTENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_item, parent, false)
                ContentViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = contentData[position]
        when (holder) {
//            is BannerViewHolder -> {
//                holder.sliderAdapter = SliderAdapter(contentData)
//                holder.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
//                holder.sliderView.setSliderAdapter(holder.sliderAdapter)
//                holder.sliderView.scrollTimeInSec = 3
//                holder.sliderView.isAutoCycle = true
//                holder.sliderView.startAutoCycle()
//            }
            is ContentViewHolder -> {

                if (currentItem.isfree.toInt() == 0) {
                    holder.premiumTextView.visibility = View.VISIBLE
                }

                holder.titleTextView.text = currentItem.name

                if (currentItem.contenttype == "playlist") {
                    val drawableStart = R.drawable.baseline_format_list_numbered_24
                    holder.descriptionText.setCompoundDrawablesWithIntrinsicBounds(
                        drawableStart,
                        0,
                        0,
                        0
                    )
                    holder.descriptionText.text = "Episodes-${currentItem.epcount}"
                } else {
                    val drawableStart = R.drawable.baseline_access_time_24
                    holder.descriptionText.setCompoundDrawablesWithIntrinsicBounds(
                        drawableStart,
                        0,
                        0,
                        0
                    )
                    holder.descriptionText.text = currentItem.length2
                }


                Glide.with(holder.imageView.context)
                    .load(currentItem.image_location)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imageView)

                holder.itemView.setOnClickListener {
                    val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
                    intent.putExtra("id", currentItem.contentid)
                    holder.itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return contentData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (contentViewType == "4") {
            TYPE_BANNER
        } else {
            TYPE_CONTENT
        }
    }

    //    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val sliderView: SliderView = itemView.findViewById(R.id.imageSlider)
//        lateinit var sliderAdapter: SliderAdapter
//    }
    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view_id)
        val titleTextView: TextView = itemView.findViewById(R.id.title_textView)
        val premiumTextView: TextView = itemView.findViewById(R.id.premiumTextView)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionTextView)
    }

}