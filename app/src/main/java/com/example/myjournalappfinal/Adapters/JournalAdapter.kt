package com.example.myjournalappfinal.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myjournalappfinal.Models.JournalEntry
import com.example.myjournalappfinal.Interfaces.JournalInteractionListener
import com.example.myjournalappfinal.R

class JournalAdapter(
    private var journalList: ArrayList<JournalEntry>,
    // 1. Add the listener to the constructor
    private val listener: JournalInteractionListener
) : RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {

    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvJournalTitle)
        val entryDate: TextView = itemView.findViewById(R.id.tvJournalDate)
        val description: TextView = itemView.findViewById(R.id.tvJournalDescription)
        val image1: ImageView = itemView.findViewById(R.id.ivJournalImage1)
        val entryTime: TextView = itemView.findViewById(R.id.txtdate)
        val image2: ImageView = itemView.findViewById(R.id.ivJournalImage2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal_entry, parent, false)
        return JournalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val model = journalList[position]

        holder.title.text = model.title
        holder.entryDate.text = "${model.entryTime}"
        holder.entryTime.text="${model.entryDate}"
        holder.description.text = model.storyContent

        // Use Glide to load images
        if (!model.imageUrl1.isNullOrEmpty()) {
            holder.image1.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load(model.imageUrl1).into(holder.image1)
        } else {
            holder.image1.visibility = View.GONE
        }

        if (!model.imageUrl2.isNullOrEmpty()) {
            holder.image2.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load(model.imageUrl2).into(holder.image2)
        } else {
            holder.image2.visibility = View.GONE
        }

        // 2. Set the click and long-click listeners
        holder.itemView.setOnClickListener {
            listener.onItemClick(model)
        }

        holder.itemView.setOnLongClickListener {
            listener.onItemLongClick(model)
            true // Important: return true to indicate the event was handled
        }
    }

    // Function to update the data in the adapter
    fun updateData(newList: List<JournalEntry>) {
        journalList.clear()
        journalList.addAll(newList)
        notifyDataSetChanged()
    }
}