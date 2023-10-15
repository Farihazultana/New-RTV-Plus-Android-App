package com.rtvplus.ui.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rtvplus.R
import com.rtvplus.data.models.single_content.single.Similarcontent
import com.rtvplus.ui.activities.PlayerActivity

class SimilarItemsAdapter(var similarContentList: List<Similarcontent>, private val listener: itemClickListener,var isPemiumUser: Int?) :
    RecyclerView.Adapter<SimilarItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.e("ssssssssssssssssssss", "item ${similarContentList.size}")

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_obj_similar_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = similarContentList[position]

        // Bind data to the views
        holder.titleTextView.text = currentItem.name
        holder.length.text = currentItem.length2
        holder.description.text = currentItem.info

        // Load image using Glide (you can use your preferred image loading library)
        Glide.with(holder.itemView.context)
            .load(currentItem.image_location)
            .placeholder(R.drawable.no_img)
            .into(holder.imageView)

        Log.e("bbbbbbbbbbbbbbbbbbbbbbbbb",isPemiumUser.toString())

        if (currentItem?.isfree == "0" && isPemiumUser == 0) {
            holder.premiumTextView.visibility = View.VISIBLE
        }
        else{
            holder.premiumTextView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(position, currentItem)
        }
    }

    override fun getItemCount(): Int {
        Log.e("ssssssssssssssssssss", "item ${similarContentList.size.toString()}")
        return similarContentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val length: TextView = itemView.findViewById(R.id.lengthTv)
        val description: TextView = itemView.findViewById(R.id.description)
        val premiumTextView: TextView = itemView.findViewById(R.id.premiumTextView)
    }

    interface itemClickListener {
        fun onItemClickListener(position: Int, item: Similarcontent?)
    }

}
