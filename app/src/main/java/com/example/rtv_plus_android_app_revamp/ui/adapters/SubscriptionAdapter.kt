package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubschemesItem
import java.util.Locale


class SubscriptionAdapter(
    var subscriptionData: List<SubschemesItem?>?,
    private val cardClickListener: CardClickListener

) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    private var selectedPositions = -1

    inner class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val packageName: TextView = itemView.findViewById(R.id.tv_packName)
        val subText: TextView = itemView.findViewById(R.id.tv_subText)
        val checkedCard: ImageView = itemView.findViewById(R.id.ig_checked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.subscription_item, parent, false)
        return SubscriptionViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: SubscriptionViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = subscriptionData?.get(position)
        Log.i("Tag", "onBindViewHolder: $item")


        if (item != null) {
            val packName = item.pack_name.lowercase(Locale.ROOT)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            holder.packageName.text = packName
        }
        if (item != null) {
            holder.subText.text = item.sub_text
        }

        holder.checkedCard.visibility =
            if (selectedPositions == position) View.VISIBLE else View.GONE



        holder.itemView.setOnClickListener {
            selectedPositions = position
            cardClickListener.onCardClickListener(position, item)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return subscriptionData?.size ?: -1
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int, item: SubschemesItem?)
    }

}