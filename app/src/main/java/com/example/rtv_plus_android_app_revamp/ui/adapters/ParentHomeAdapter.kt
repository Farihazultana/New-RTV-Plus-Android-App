package com.example.rtv_plus_android_app_revamp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rtv_plus_android_app_revamp.R
import com.example.rtv_plus_android_app_revamp.data.models.home.Data

class ParentHomeAdapter(var homeData: List<Data>) :
    RecyclerView.Adapter<ParentHomeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_obj_parent_home_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = homeData[position]


        if (currentItem.contentviewtype == "5") {
            holder.textView.visibility = View.GONE
        } else {
            holder.textView.text = currentItem.catname
        }

        holder.childListAdapter =
            ChildHomeAdapter(currentItem.contents, contentViewType = currentItem.contentviewtype)
        holder.recyclerView.layoutManager = LinearLayoutManager(
            holder.recyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.recyclerView.adapter = holder.childListAdapter
    }

    override fun getItemCount(): Int {
        return homeData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var childListAdapter: ChildHomeAdapter
        val recyclerView: RecyclerView = itemView.findViewById(R.id.listItem_recyclerview)
        val textView: TextView = itemView.findViewById(R.id.title_textviewID)
    }
}