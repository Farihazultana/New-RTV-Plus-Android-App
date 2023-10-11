package com.rtvplus.ui.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rtvplus.R
import com.rtvplus.data.models.home.Content
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.ui.activities.PlayerActivity
import com.rtvplus.utils.AppUtils.GoogleSignInKey
import com.rtvplus.utils.AppUtils.LogInKey
import com.rtvplus.utils.SharedPreferencesUtil

class ChildHomeAdapter(
    private var myContext: Context,
    private var contentData: List<Content>,
    private var contentViewType: String,
    private val navController: NavController,
    var isPemiumUser: Int?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_BANNER = 0
        private const val TYPE_CONTENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            TYPE_BANNER -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.row_obj_banner, parent, false)
//                BannerViewHolder(view)
//            }
            TYPE_CONTENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_obj_child_home_data, parent, false)
                ContentViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = contentData[position]
        when (holder) {
//            is BannerViewHolder -> {
//                holder.sliderAdapter = SliderAdapter(contentData)
//                holder.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
//                holder.sliderView.setSliderAdapter(holder.sliderAdapter)
//                holder.sliderView.scrollTimeInSec = 3
//                holder.sliderView.isAutoCycle = true
//                holder.sliderView.startAutoCycle()
//            }
            is ContentViewHolder -> {
                if (currentItem.isfree == "0" && isPemiumUser.toString() == "0") {
                    holder.premiumTextView.visibility = View.VISIBLE
                } else {
                    holder.premiumTextView.visibility = View.GONE
                }

                holder.titleTextView.text = currentItem.name

                if (currentItem.contenttype == "playlist") {
                    val drawableStart = R.drawable.baseline_format_list_numbered_24
                    holder.descriptionText.setCompoundDrawablesWithIntrinsicBounds(
                        drawableStart, 0, 0, 0
                    )
                    holder.descriptionText.text = "Episodes-${currentItem.epcount}"

                    holder.itemView.setOnClickListener {
                        val spRes = SharedPreferencesUtil.getData(
                            myContext, LogInKey, ""
                        )
                        val spResGoogle = SharedPreferencesUtil.getData(
                            myContext, GoogleSignInKey, ""
                        )
                        if (spRes.toString().isNotEmpty() || spResGoogle.toString().isNotEmpty()) {

                            if (isPemiumUser.toString() == "0" && currentItem.isfree == "0") {
                                navController.navigate(R.id.SubscriptionFragment)

                            } else {
                                val intent =
                                    Intent(holder.itemView.context, PlayerActivity::class.java)
                                intent.putExtra("id", currentItem.contentid)
                                holder.itemView.context.startActivity(intent)
                            }

                        } else {
                            val intent = Intent(holder.itemView.context, LoginActivity::class.java)
                            holder.itemView.context.startActivity(intent)
                        }
                    }

                } else {
                    val drawableStart = R.drawable.baseline_access_time_24
                    holder.descriptionText.setCompoundDrawablesWithIntrinsicBounds(
                        drawableStart, 0, 0, 0
                    )
                    holder.descriptionText.text = currentItem.length2

                    holder.itemView.setOnClickListener {
                        val spRes = SharedPreferencesUtil.getData(
                            myContext, LogInKey, ""
                        )
                        val spResGoogle = SharedPreferencesUtil.getData(
                            myContext, GoogleSignInKey, ""
                        )
                        Log.i("SPref", "onBindViewHolder: $spRes")

                        if (spRes.toString().isNotEmpty() || spResGoogle.toString().isNotEmpty()) {
                            if (isPemiumUser.toString() == "0" && currentItem.isfree == "0") {
                                navController.navigate(R.id.SubscriptionFragment)

                            } else {
                                val intent =
                                    Intent(holder.itemView.context, PlayerActivity::class.java)
                                intent.putExtra("id", currentItem.contentid)
                                intent.putExtra("type", "single")
                                holder.itemView.context.startActivity(intent)
                            }

                        } else {
                            val intent = Intent(holder.itemView.context, LoginActivity::class.java)
                            holder.itemView.context.startActivity(intent)
                        }


                    }
                }

                Glide.with(holder.imageView.context).load(currentItem.image_location)
                    .placeholder(R.drawable.no_img).into(holder.imageView)


            }
        }
    }

    override fun getItemCount(): Int {
        return contentData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (contentViewType == "4") {
            TYPE_BANNER
        } else {
            TYPE_CONTENT
        }
    }

    //    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val sliderView: SliderView = itemView.findViewById(R.id.imageSlider)
//        lateinit var sliderAdapter: SliderAdapter
//    }
    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view_id)
        val titleTextView: TextView = itemView.findViewById(R.id.title_textView)
        val premiumTextView: TextView = itemView.findViewById(R.id.premiumTextView)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionTextView)
    }

}