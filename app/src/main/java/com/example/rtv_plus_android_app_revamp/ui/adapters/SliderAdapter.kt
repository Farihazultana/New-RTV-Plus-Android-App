package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.home.Content
import com.example.rtv_plus_android_app_revamp.ui.activities.PlayerActivity
import com.smarteist.autoimageslider.SliderViewAdapter
class SliderAdapter(imageUrl: List<Content>) :
    SliderViewAdapter<SliderAdapter.SliderViewHolder>() {
    var sliderList: List<Content> = imageUrl
    override fun getCount(): Int {
        return sliderList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapter.SliderViewHolder {
        val inflate: View =
            LayoutInflater.from(parent!!.context).inflate(R.layout.row_obj_slider_view, null)
        return SliderViewHolder(inflate)
    }
    override fun onBindViewHolder(viewHolder: SliderAdapter.SliderViewHolder?, position: Int) {
        if (viewHolder != null) {
            Glide.with(viewHolder.itemView).load(sliderList[position].image_location)
                .into(viewHolder.imageView)

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(viewHolder.itemView.context, PlayerActivity::class.java)
                intent.putExtra("id", sliderList[position].contentid)
                viewHolder.itemView.context.startActivity(intent)
            }
        }
    }
    class SliderViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {
        var imageView: ImageView = itemView!!.findViewById(R.id.myimage)
    }
}