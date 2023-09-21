package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.seeAll.Content

class SeeAllAdapter (var seeAllData: List<Content?>?) :
    RecyclerView.Adapter<SeeAllAdapter.SeeAllViewHolder>() {

    inner class SeeAllViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var contentImage: ImageView = itemView.findViewById(R.id.image_view_id)
        var contentTitle: TextView = itemView.findViewById(R.id.title_textView)
        var contentDuration: TextView = itemView.findViewById(R.id.descriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeAllViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return SeeAllViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return seeAllData?.size ?: -1
    }

    override fun onBindViewHolder(holder: SeeAllViewHolder, position: Int) {
        val item = seeAllData?.get(position)
        if (item != null){
            Glide.with(holder.itemView.context)
                .load(item.image_location)
                .into(holder.contentImage)
        }
        if (item != null){
            holder.contentTitle.text = item.name
        }
        if (item != null){
            holder.contentDuration.text = item.duration
        }
    }

    interface itemClickListener{
        fun onItemClickListener(position: Int, item: Content?)
    }
}