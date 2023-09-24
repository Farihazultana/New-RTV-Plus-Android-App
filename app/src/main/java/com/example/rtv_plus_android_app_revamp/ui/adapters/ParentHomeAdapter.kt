package com.example.rtv_plus_android_app_revamp.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.home.Data
import com.smarteist.autoimageslider.SliderView

class ParentHomeAdapter(var homeData: List<Data>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_BANNER = 0
        private const val TYPE_CONTENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_BANNER -> {
                val view = inflater.inflate(R.layout.row_obj_banner, parent, false)
                BannerViewHolder(view)
            }

            TYPE_CONTENT -> {
                val view = inflater.inflate(R.layout.row_obj_parent_home_data, parent, false)
                ContentViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = homeData[position]

        when (holder) {
            is ContentViewHolder -> {
                if (!currentItem.contents.isNullOrEmpty()) {
                    holder.childListAdapter =
                        ChildHomeAdapter(currentItem.contents, currentItem.contentviewtype)
                    holder.recyclerView.layoutManager = LinearLayoutManager(
                        holder.recyclerView.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    holder.textView.text = currentItem.catname
                    holder.recyclerView.adapter = holder.childListAdapter
                } else {
                    holder.textView.visibility = View.GONE
                    holder.recyclerView.visibility = View.GONE
                    holder.seeAll.visibility = View.GONE
                }
            }

            is BannerViewHolder -> {
                holder.sliderAdapter = SliderAdapter(currentItem.contents)
                holder.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
                holder.sliderView.setSliderAdapter(holder.sliderAdapter)
                holder.sliderView.scrollTimeInSec = 3
                holder.sliderView.isAutoCycle = true
                holder.sliderView.startAutoCycle()
            }
        }
    }

    override fun getItemCount(): Int {
        return homeData.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentItem = homeData[position]
        return if (currentItem.contentviewtype == "4") {
            TYPE_BANNER
        } else {
            TYPE_CONTENT
        }
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var childListAdapter: ChildHomeAdapter
        val recyclerView: RecyclerView = itemView.findViewById(R.id.listItem_recyclerview)
        val textView: TextView = itemView.findViewById(R.id.title_textviewID)
        val seeAll: TextView = itemView.findViewById(R.id.seeAll)
    }

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sliderView: SliderView = itemView.findViewById(R.id.imageSlider)
        lateinit var sliderAdapter: SliderAdapter
    }
}