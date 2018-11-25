package com.example.essam.instgramfillter.Adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.essam.instgramfillter.Interface.FilterImageListener
import com.example.essam.instgramfillter.R
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.android.synthetic.main.thumbnail_list_item.view.*

class ThumbnailAdapter(
    var context: Context,
    var listThumbnail: List<ThumbnailItem>,
    var listener: FilterImageListener
) :
    RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {

    var selectedItem = 0
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.thumbnail_list_item, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return listThumbnail.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thumbnailItem = listThumbnail[position]
        holder.thumbnail.setImageBitmap(thumbnailItem.image)
        holder.filterName.text = thumbnailItem.filterName
        holder.thumbnail.setOnClickListener {
            listener.onFilterImageSelected(thumbnailItem.filter)
            selectedItem = position
            notifyDataSetChanged()
        }
        if (selectedItem == position) {
            holder.filterName.setTextColor(ContextCompat.getColor(context, R.color.filter_label_selected))
        } else
            holder.filterName.setTextColor(ContextCompat.getColor(context, R.color.filter_label_normal))
    }


    class ViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        var thumbnail: ImageView
        var filterName: TextView

        init {
            thumbnail = viewItem.thumbnail
            filterName = viewItem.filter_name
        }

    }
}