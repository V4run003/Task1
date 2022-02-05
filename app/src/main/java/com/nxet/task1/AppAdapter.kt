package com.nxet.task1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nxet.task1.Interfaces.RecyclerItemClick
import com.nxet.task1.Models.AppModel
import de.hdodenhof.circleimageview.CircleImageView

class AppAdapter(
    private val context: Context,
    private val apps: ArrayList<AppModel>,
    private val recyclerItemClick: RecyclerItemClick
) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = apps[position]
        holder.icon.setImageDrawable(item.appIcon)
        holder.appname.text = item.appName

        holder.itemView.setOnClickListener {
            recyclerItemClick.OnClick(item.packageName)

        }

    }

    override fun getItemCount(): Int {
        return apps.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: CircleImageView = itemView.findViewById(R.id.icon)
        var appname: TextView = itemView.findViewById(R.id.appName)


    }


}