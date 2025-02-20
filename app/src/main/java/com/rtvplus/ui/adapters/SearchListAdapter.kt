package com.rtvplus.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rtvplus.R
import com.rtvplus.data.models.search.Content

class SearchListAdapter(
    var content: List<Content?>?,
    private val listener: itemClickListener,
    var isPemiumUser: Int?
) :
    RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_obj_search_item, parent, false)
        return SearchListViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return content?.size ?: -1
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        val item = content?.get(position)
        if (item != null) {
            Glide.with(holder.itemView.context)
                .load(item.image_location)
                .placeholder(R.drawable.no_img)
                .into(holder.contentImage)
        }
        if (item?.isfree?.toInt() == 0 && isPemiumUser == 0) {
            holder.premiumText.visibility = View.VISIBLE
        } else {
            holder.premiumText.visibility = View.GONE
        }
        if (item != null) {
            holder.contentTitle.text = item.name
        }
        val drawableStart = R.drawable.baseline_access_time_24
        holder.contentDuration.setCompoundDrawablesWithIntrinsicBounds(
            drawableStart,
            0,
            0,
            0
        )
        holder.contentDuration.text = item?.length2
        Log.i("TagN", "onBindViewHolder: $item")

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(position, item)
        }
    }

    inner class SearchListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentImage: ImageView = itemView.findViewById(R.id.image_view_id)
        var premiumText: TextView = itemView.findViewById(R.id.premiumTextView)
        var contentTitle: TextView = itemView.findViewById(R.id.title_textView)
        var contentDuration: TextView = itemView.findViewById(R.id.descriptionTextView)
    }

    interface itemClickListener {
        fun onItemClickListener(position: Int, item: Content?)
    }
}