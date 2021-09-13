package com.example.memorycollector.adapters

import android.location.Geocoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycollector.R
import com.example.memorycollector.models.MemoryItem
import com.example.memorycollector.screens.ViewMemoryListFragment

class MemoryItemViewAdapter(private var list: List<MemoryItem>, private val listener: ViewMemoryListFragment) : RecyclerView.Adapter<MemoryItemViewAdapter.MemoryItemViewHolder>() {

    inner class MemoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
        var textViewDate: TextView = itemView.findViewById(R.id.text_view_date)
        var imageView: ImageView = itemView.findViewById(R.id.image_view)
        var textViewLocation: TextView = itemView.findViewById(R.id.text_view_location)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.memory_item_view, parent, false)
        return MemoryItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoryItemViewHolder, position: Int) {
        val currentItem = list[position]
        holder.textViewTitle.text = currentItem.title
        holder.textViewDate.text = currentItem.dateString
        holder.imageView.setImageURI(Uri.parse(currentItem.imageString))
        holder.textViewLocation.text = currentItem.locationString

    }

    override fun getItemCount() = list.size


    interface OnItemClickListener {
        fun onItemClick(position: Int)

    }
}