package com.rtvplus.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
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
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.SharedPreferencesUtil
import java.util.Locale


class SubscriptionAdapter(
    private val cardClickListener: CardClickListener,
    private val context : Context

) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    private var selectedPositions = -1
    var subscriptionData: ArrayList<SubschemesItem> = ArrayList()


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

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: SubscriptionViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val subschemeItem = subscriptionData?.get(position)
        Log.i("Tag", "onBindViewHolder: $subschemeItem")

        if (subschemeItem != null) {
            val packName = subschemeItem.pack_name.lowercase(Locale.ROOT)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            holder.packageName.text = packName
        }
        if (subschemeItem != null) {
            holder.subText.text = subschemeItem.sub_text
        }

        holder.checkedCard.visibility =
            if (selectedPositions == position) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            if (subschemeItem?.userpack == "nopack") {
                selectedPositions = position
                cardClickListener.onCardClickListener(position, subschemeItem)
            } else {
                holder.itemView.isClickable = false
            }
        }


        val loginPackcode = SharedPreferencesUtil.getSavedLogInData(context)?.packcode
        Log.i("SubAdap", "onBindViewHolder pack subscribed: [login] $loginPackcode == ${subschemeItem?.sub_pack} [Subscription]")

        if (loginPackcode == subschemeItem?.sub_pack || selectedPositions == position) {
            if (loginPackcode == subschemeItem?.sub_pack) {
                holder.packCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        holder.packCard.context,
                        R.color.green_lite
                    )
                )
            }
            holder.checkedCard.visibility = View.VISIBLE

            return
        } else {
            holder.checkedCard.visibility = View.GONE
            holder.packCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.packCard.context,
                    R.color.appwhite
                )
            )
        }

    }

    fun setData(subschemes: ArrayList<SubschemesItem>, selectedPositions: Int) {
        if (subscriptionData.isNotEmpty()){
            subscriptionData.clear()
        }
        this.subscriptionData = subschemes
        this.selectedPositions = selectedPositions
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return subscriptionData.size
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int, item: SubschemesItem?)
    }

}