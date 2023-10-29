package com.rtvplus.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rtvplus.R
import com.rtvplus.data.models.home.Content
import com.rtvplus.ui.activities.LoginActivity
import com.rtvplus.ui.activities.PlayerActivity
import com.rtvplus.ui.fragments.HomeFragment
import com.rtvplus.ui.fragments.subscription.SubscriptionFragment
import com.rtvplus.utils.AppUtils.UsernameInputKey
import com.rtvplus.utils.SharedPreferencesUtil

class ChildHomeAdapter(
    private var myContext: Context,
    private var contentData: List<Content>,
    private var contentViewType: String,
    var isPemiumUser: Int?,
    private val homeFragment: HomeFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_BANNER = 0
        private const val TYPE_CONTENT = 1
        private const val TYPE_CONTINUE_WATCHING = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CONTENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_obj_child_home_data, parent, false)
                ContentViewHolder(view)
            }

            TYPE_CONTINUE_WATCHING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_obj_continue_watching_data, parent, false)
                ContinueWatchingViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = contentData[position]
        when (holder) {

            is ContentViewHolder -> {
                if (currentItem.isfree == "0" && isPemiumUser == 0) {
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
                        val username = SharedPreferencesUtil.getData(
                            myContext, UsernameInputKey, ""
                        )
                        if (username.toString().isNotEmpty()) {
                            if (isPemiumUser.toString() == "0" && currentItem.isfree == "0") {

                                val bundle = Bundle()
                                bundle.putString("nav_key", "from_child_fregment")
                                //  navController.navigate(R.id.SubscriptionFragment, bundle)
                                val subscriptionFragment = SubscriptionFragment()
                                homeFragment.bindFragment(subscriptionFragment)

                            } else {
                                val intent =
                                    Intent(holder.itemView.context, PlayerActivity::class.java)
                                intent.putExtra("id", currentItem.contentid)
                                intent.putExtra("ct", currentItem.catcode)
                                holder.itemView.context.startActivity(intent)
                            }

                        } else {
                            val intent = Intent(holder.itemView.context, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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

                        val username = SharedPreferencesUtil.getData(
                            myContext, UsernameInputKey, ""
                        )
                        if (username.toString().isNotEmpty()) {
                            if (isPemiumUser.toString() == "0" && currentItem.isfree == "0") {
                                val bundle = Bundle()
                                bundle.putString("nav_key", "from_child_fregment")

                                val subscriptionFragment = SubscriptionFragment()
                                homeFragment.bindFragment(subscriptionFragment)

                                // navController.navigate(R.id.SubscriptionFragment, bundle)

                            } else {
                                val intent =
                                    Intent(holder.itemView.context, PlayerActivity::class.java)
                                intent.putExtra("id", currentItem.contentid)
                                intent.putExtra("type", "single")
                                holder.itemView.context.startActivity(intent)
                            }

                        } else {
                            val intent = Intent(holder.itemView.context, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            holder.itemView.context.startActivity(intent)
                        }
                    }
                }
                Glide.with(holder.imageView.context).load(currentItem.image_location)
                    .placeholder(R.drawable.no_img).into(holder.imageView)
            }

            is ContinueWatchingViewHolder -> {

                holder.progressBar.max = currentItem.lengthinsec.toInt()

                holder.progressBar.progress = currentItem.playposition.toInt()


                if (currentItem.isfree == "0" && isPemiumUser == 0) {
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
                        val username = SharedPreferencesUtil.getData(
                            myContext, UsernameInputKey, ""
                        )
                        if (username.toString().isNotEmpty()) {
                            if (isPemiumUser.toString() == "0" && currentItem.isfree == "0") {

                                val bundle = Bundle()
                                bundle.putString("nav_key", "from_child_fregment")
                                // navController.navigate(R.id.SubscriptionFragment, bundle)
                                val subscriptionFragment = SubscriptionFragment()
                                homeFragment.bindFragment(subscriptionFragment)

                            } else {
                                val intent =
                                    Intent(holder.itemView.context, PlayerActivity::class.java)
                                intent.putExtra("id", currentItem.contentid)
                                intent.putExtra("ct", currentItem.catcode)
                                holder.itemView.context.startActivity(intent)
                            }

                        } else {
                            val intent = Intent(holder.itemView.context, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            holder.itemView.context.startActivity(intent)
                        }
                    }

                } else {
                    val drawableStart = R.drawable.baseline_access_time_24
                    holder.descriptionText.setCompoundDrawablesWithIntrinsicBounds(
                        drawableStart, 0, 0, 0
                    )
                    holder.descriptionText.text = currentItem.duration
                    holder.itemView.setOnClickListener {

                        val username = SharedPreferencesUtil.getData(
                            myContext, UsernameInputKey, ""
                        )
                        if (username.toString().isNotEmpty()) {
                            if (isPemiumUser.toString() == "0" && currentItem.isfree == "0") {
                                val bundle = Bundle()
                                bundle.putString("nav_key", "from_child_fregment")
                                //  navController.navigate(R.id.SubscriptionFragment, bundle)
                                val subscriptionFragment = SubscriptionFragment()
                                homeFragment.bindFragment(subscriptionFragment)

                            } else {

                                val watchedTime = (currentItem.playposition.toInt()) * 1000

                                Log.e("WatchedTimeInsideAdapter", watchedTime.toString())

                                val intent =
                                    Intent(holder.itemView.context, PlayerActivity::class.java)
                                intent.putExtra("id", currentItem.contentid)
                                intent.putExtra("type", "single")
                                intent.putExtra("isplayed", watchedTime.toString())
                                holder.itemView.context.startActivity(intent)
                            }

                        } else {
                            val intent = Intent(holder.itemView.context, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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
        } else if (contentViewType == "2") {
            TYPE_CONTINUE_WATCHING
        } else {
            TYPE_CONTENT
        }
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view_id)
        val titleTextView: TextView = itemView.findViewById(R.id.title_textView)
        val premiumTextView: TextView = itemView.findViewById(R.id.premiumTextView)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionTextView)
    }

    inner class ContinueWatchingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view_id)
        val titleTextView: TextView = itemView.findViewById(R.id.title_textView)
        val premiumTextView: TextView = itemView.findViewById(R.id.premiumTextView)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionTextView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

}