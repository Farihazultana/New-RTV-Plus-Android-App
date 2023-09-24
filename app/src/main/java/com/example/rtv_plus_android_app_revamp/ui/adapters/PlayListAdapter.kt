package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.single_content.playlist.Episodelist

class PlayListAdapter(var episodeList: List<Episodelist>) :
    RecyclerView.Adapter<PlayListAdapter.ViewHolder>() {

    private var selectedItemPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_obj_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = episodeList[position]

        // Bind data to the views
        holder.titleTextView.text = currentItem.title
        holder.length.text = currentItem.duration

        Glide.with(holder.itemView.context)
            .load(currentItem.image)
            .into(holder.imageView)

        // Check if the current item is selected and playing
        val isSelectedAndPlaying = position == selectedItemPosition

        // Change the background color based on selection
        if (isSelectedAndPlaying) {
            holder.itemView.setBackgroundResource(R.color.sliderIndicatorColor)
        } else {
            holder.itemView.setBackgroundResource(R.color.white)
        }

        // Set the visibility of the isPlaying TextView
        holder.isPlaying.visibility = if (isSelectedAndPlaying) View.VISIBLE else View.GONE

        // Set an item click listener
        holder.itemView.setOnClickListener {
            // Update the selected item position
            val previousSelectedItemPosition = selectedItemPosition
            selectedItemPosition = holder.adapterPosition

            // Notify item changes to redraw the views
            notifyItemChanged(previousSelectedItemPosition)
            notifyItemChanged(selectedItemPosition)

            // Handle item click
//            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
//            intent.putExtra("id", currentItem.mediaid)
//            intent.putExtra("type", "playlist")
//            holder.itemView.context.startActivity(intent)
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
}
