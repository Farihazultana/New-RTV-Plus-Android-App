package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.Content
import com.example.rtv_plus_android_app_revamp.ui.activities.PlayerActivity


class FavoriteListAdapter(
    var content: List<Content?>? = null,
    private val editItemClickListener: OnRemoveItemClickListener
) :
    RecyclerView.Adapter<FavoriteListAdapter.FavoriteListViewHolder>() {
    interface OnRemoveItemClickListener {
        fun onRemoveItemClicked(contentId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_obj_favorite_list_item, parent, false)
        return FavoriteListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return content?.size ?: -1
    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        val curentItem = content?.get(position)
        if (curentItem != null) {
            Glide.with(holder.itemView.context)
                .load(curentItem.image_location)
                .placeholder(R.drawable.no_img)
                .into(holder.contentImage)
        }
        if (curentItem?.isfree?.toInt() == 0) {
            holder.premiumText.visibility = View.VISIBLE
        }
        if (curentItem != null) {
            holder.contentTitle.text = curentItem.name
        }
        val drawableStart = R.drawable.baseline_access_time_24
        holder.contentDuration.setCompoundDrawablesWithIntrinsicBounds(
            drawableStart,
            0,
            0,
            0
        )
        holder.contentDuration.text = curentItem?.length2

        holder.optionMenu.setOnClickListener { v ->
            val popupMenu = PopupMenu(v.context, v)
            popupMenu.inflate(R.menu.remove_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        curentItem?.contentid?.let { editItemClickListener.onRemoveItemClicked(it) }
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
            intent.putExtra("id", curentItem?.contentid)
            intent.putExtra("type", "single")
            holder.itemView.context.startActivity(intent)
        }
    }

    inner class FavoriteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentImage: ImageView = itemView.findViewById(R.id.image_view_id)
        var premiumText: TextView = itemView.findViewById(R.id.premiumTextView)
        var contentTitle: TextView = itemView.findViewById(R.id.title_textView)
        var contentDuration: TextView = itemView.findViewById(R.id.descriptionTextView)
        var optionMenu: ImageView = itemView.findViewById(R.id.optionMenu)
    }
}