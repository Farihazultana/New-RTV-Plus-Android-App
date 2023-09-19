package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.subscription.SubschemesItem


class SubscriptionAdapter(var subscriptionData:List<SubschemesItem?>?, private val cardClickListener: CardClickListener) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    private var selectedPosition: Int = -1
    inner class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val packageName : TextView = itemView.findViewById(R.id.tv_packName)
        val subText: TextView = itemView.findViewById(R.id.tv_subText)
        val checkedCard: ImageView = itemView.findViewById(R.id.ig_checked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.subscription_item, parent, false)
        return SubscriptionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val item = subscriptionData?.get(position)
        if (item != null) {
            holder.packageName.text = item.packName
        }
        if (item != null) {
            holder.subText.text = item.subText
        }


        holder.itemView.setOnClickListener {
            cardClickListener.onCardClickListener(item)
            holder.checkedCard.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return subscriptionData?.size ?: 0
    }

    interface CardClickListener{
        fun onCardClickListener(item: SubschemesItem?)
    }


}