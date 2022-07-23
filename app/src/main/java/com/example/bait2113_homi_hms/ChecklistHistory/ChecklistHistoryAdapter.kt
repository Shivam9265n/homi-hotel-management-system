package com.example.bait2113_homi_hms.ChecklistHistory

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.R

class ChecklistHistoryAdapter(
        private val context: Context,
        private val checklistHistoryData: MutableList<ChecklistHistoryModel>

) : RecyclerView.Adapter<ChecklistHistoryAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomNo: TextView = view.findViewById<TextView>(R.id.txt_checklist_roomNo)
        val roomType: TextView = view.findViewById<TextView>(R.id.txt_checklist_roomType)
        val date: TextView = view.findViewById<TextView>(R.id.txt_checklist_His_date)
        val time: TextView = view.findViewById<TextView>(R.id.txt_checklist_His_time)
        val housekeeper: TextView = view.findViewById<TextView>(R.id.txt_checklist_housekeeper)
        val image: ImageView = view.findViewById(R.id.room_Image_Checklist)
    }

    override fun getItemCount(): Int {
        return checklistHistoryData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.checklist_history_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = checklistHistoryData[position]
        holder.roomNo.text = item.roomName
        holder.roomType.text = item.roomCat
        holder.date.text = item.dateCreated
        holder.time.text = item.timeCreated
        holder.housekeeper.text = item.housekeeper
        Glide.with(context).load(item.roomImage).into(holder.image)
    }

    fun onClean() {
        checklistHistoryData.clear()
    }
}