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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rtvplus.R
import com.rtvplus.data.models.logIn.LogInModuleItem
import com.rtvplus.data.models.subscription.SubschemesItem
import com.rtvplus.utils.AppUtils
import com.rtvplus.utils.AppUtils.LogInModule
import com.rtvplus.utils.SharedPreferencesUtil
import java.util.Locale
import javax.inject.Inject


class SubscriptionAdapter(
    private val cardClickListener: CardClickListener,
    private val context : Context

) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    private var selectedPositions = -1
    var subscriptionData: ArrayList<SubschemesItem> = ArrayList()

//    @Inject
//    lateinit var logInModule: LogInModuleItem

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
            if (item?.userpack == "nopack"){
                selectedPositions = position
                cardClickListener.onCardClickListener(position, item)
            }else{
                holder.itemView.isClickable = false
            }
        }

        //logInModule[0].packcode
        //Log.i("sub", "Login Module onBindViewHolder: ${logInModule[0].packcode}")
        val loginPackcode = SharedPreferencesUtil.getData(context, AppUtils.LogIn_packcode, "")
        Log.i("SubLog", "onBindViewHolder: $loginPackcode")

        var loginDataStore = SharedPreferencesUtil.getData(context, AppUtils.LogIn_packcode, "")
        Log.i("SubAdapLog", "onBindViewHolder: $loginDataStore")
        Log.i("SubAdapLog", "onBindViewHolder item sub pack: ${item?.sub_pack}")


        //var dummy = SharedPreferencesUtil.getData(context,LogInModule,"")
        if(loginDataStore == item?.sub_pack || selectedPositions == position){
            if (loginDataStore == item?.sub_pack){
                holder.packCard.setCardBackgroundColor(ContextCompat.getColor(holder.packCard.context, R.color.green_lite))
            }
            holder.checkedCard.visibility = View.VISIBLE

            return
        }else{
            holder.checkedCard.visibility = View.GONE
            holder.packCard.setCardBackgroundColor(ContextCompat.getColor(holder.packCard.context, R.color.appwhite))
        }

    }

    fun setData(subschemes: ArrayList<SubschemesItem>) {
        if (subscriptionData.isNotEmpty()){
            subscriptionData.clear()
        }
        this.subscriptionData = subschemes
        selectedPositions = -1
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return subscriptionData.size ?: -1
    }

    interface CardClickListener {
        fun onCardClickListener(position: Int, item: SubschemesItem?)
    }

}