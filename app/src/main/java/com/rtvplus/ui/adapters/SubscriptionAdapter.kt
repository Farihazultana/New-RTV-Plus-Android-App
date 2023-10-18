package com.rtvplus.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rtvplus.R
import com.rtvplus.data.models.subscription.SubschemesItem
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
        val packCard : CardView = itemView.findViewById(R.id.cvPack)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.subscription_item, parent, false)
        return SubscriptionViewHolder(itemView)
    }

    fun updateData(newData: List<SubschemesItem?>) {
        subscriptionData = newData
        notifyDataSetChanged()
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


        if(item?.userpack == item?.sub_pack){
            holder.packCard.setCardBackgroundColor(ContextCompat.getColor(holder.packCard.context, R.color.green_lite))
            holder.checkedCard.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            if (item?.userpack == "nopack"){
                selectedPositions = position
                cardClickListener.onCardClickListener(position, item)
                notifyDataSetChanged()
            }else{
                holder.itemView.isClickable = false
            }

        }
    }

    override fun getItemCount(): Int {
        return subscriptionData?.size ?: -1
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int, item: SubschemesItem?)
    }

}