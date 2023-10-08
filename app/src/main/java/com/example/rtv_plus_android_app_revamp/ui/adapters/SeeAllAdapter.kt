package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.seeAll.Content
import com.example.rtv_plus_android_app_revamp.ui.activities.LoginActivity
import com.example.rtv_plus_android_app_revamp.ui.activities.PlayerActivity
import com.example.rtv_plus_android_app_revamp.utils.AppUtils
import com.example.rtv_plus_android_app_revamp.utils.SharedPreferencesUtil

class SeeAllAdapter(private var myContext: Context, var seeAllData: List<Content?>?) :
    RecyclerView.Adapter<SeeAllAdapter.SeeAllViewHolder>() {

    inner class SeeAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentImage: ImageView = itemView.findViewById(R.id.image_view_id)
        var premiumText: TextView = itemView.findViewById(R.id.premiumTextView)
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
        if (item != null) {
            Glide.with(holder.itemView.context)
                .load(item.image_location)
                .placeholder(R.drawable.no_img)
                .into(holder.contentImage)
        }
        if (item?.isfree?.toInt() == 0) {
            holder.premiumText.visibility = View.VISIBLE
        }
        if (item != null) {
            holder.contentTitle.text = item.name
        }
//        if (item != null){
//            holder.contentDuration.text = item.duration
//        }

        if (item?.contenttype == "playlist") {
            val drawableStart = R.drawable.baseline_format_list_numbered_24
            holder.contentDuration.setCompoundDrawablesWithIntrinsicBounds(
                drawableStart,
                0,
                0,
                0
            )
            holder.contentDuration.text = "Episodes-${item.epcount}"
            Log.i("TagM", "onBindViewHolder: $item")
        } else {
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
                val spRes = SharedPreferencesUtil.getData(
                    myContext,
                    AppUtils.LogInKey,
                    ""
                )
                val spResGoogle = SharedPreferencesUtil.getData(
                    myContext,
                    AppUtils.GoogleSignInKey,
                    ""
                )
                if (spRes.toString().isNotEmpty() || spResGoogle.toString().isNotEmpty()) {
                    val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
                    intent.putExtra("id", item?.contentid)
                    intent.putExtra("type", "single")
                    holder.itemView.context.startActivity(intent)
                } else {
                    val intent = Intent(holder.itemView.context, LoginActivity::class.java)
                    holder.itemView.context.startActivity(intent)
                }
            }
        }


    }

    interface itemClickListener {
        fun onItemClickListener(position: Int, item: Content?)
    }
}