package com.example.rtv_plus_android_app_revamp.ui.adapters
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.home.Content
class IndicatorAdapter(contentList: List<Content>) :
    RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder>() {
    var sliderList: List<Content> = contentList
    private val sliderAdapter: SliderAdapter = SliderAdapter(contentList)

    init {
        // Set a position change listener for the sliderAdapter
        sliderAdapter.setPositionChangeListener { newPosition ->
            Log.e("GlobalPosition", "Global position changed to: $newPosition")
            notifyDataSetChanged() // Notify the adapter to refresh the views
        }

        val position = sliderAdapter.getGlobalPosition()
        Log.e("GlobalPosition", position.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorViewHolder {
       // Log.e("cccccccccccccc",newPosition.toString())
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.row_obj_slider_indicator, parent, false)
        return IndicatorViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        Log.e("bbbbbbbbbbbbbbbbb", sliderList.size.toString())
        return sliderList.size

    }

    override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {

        holder.indicatorDot.setImageResource(R.drawable.selected_indicator_dot)
    }

    class IndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var indicatorDot: ImageView = itemView.findViewById(R.id.indicatorDot)
    }
}

