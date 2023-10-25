package com.rtvplus.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rtvplus.R
import com.rtvplus.data.models.single_content.playlist.Episodelist

class PlayListAdapter(var episodeList: List<Episodelist>) :
    RecyclerView.Adapter<PlayListAdapter.ViewHolder>() {

    private var selectedItemPosition = 0
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_obj_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentItem = episodeList[position]
        holder.titleTextView.text = currentItem.title
        holder.length.text = currentItem.duration

        Glide.with(holder.itemView.context)
            .load(currentItem.image)
            .placeholder(R.drawable.no_img)
            .into(holder.imageView)

        val isSelectedAndPlaying = position == selectedItemPosition

        if (isSelectedAndPlaying) {
            holder.itemView.setBackgroundResource(R.color.sliderIndicatorColor)
        } else {
            holder.itemView.setBackgroundResource(R.color.appwhite)
        }
        holder.isPlaying.visibility = if (isSelectedAndPlaying) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val previousSelectedItemPosition = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(previousSelectedItemPosition)
            notifyItemChanged(selectedItemPosition)
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return episodeList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val length: TextView = itemView.findViewById(R.id.lengthTv)
        val isPlaying: TextView = itemView.findViewById(R.id.isPlaying)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}
