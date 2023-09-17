package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.home.Content

class ChildHomeAdapter(private var contentData: List<Content>, private var contentViewType: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_BANNER = 0
        private const val TYPE_CONTENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_BANNER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_obj_child_home_data, parent, false)
                BannerViewHolder(view)
            }
            TYPE_CONTENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_obj_child_home_data, parent, false)
                ContentViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = contentData[position]
        when (holder) {
//            is BannerViewHolder -> {
//                Glide.with(holder.bannerImageView)
//                    .load(currentItem.image_location)
//                    .placeholder(R.drawable.ic_launcher_background)
//                    .into(holder.bannerImageView)
//            }
            is ContentViewHolder -> {

                Glide.with(holder.imageView.context)
                    .load(currentItem.image_location)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imageView)
            }
        }
    }

    override fun getItemCount(): Int {
        return contentData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (contentViewType == "5") {
            TYPE_BANNER
        }
        else {
            TYPE_CONTENT
        }
    }

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerImageView: ImageView = itemView.findViewById(R.id.image_view_id)
    }
    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view_id)
        val titleTextView: TextView = itemView.findViewById(R.id.title_textView)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionTextView)
    }


}