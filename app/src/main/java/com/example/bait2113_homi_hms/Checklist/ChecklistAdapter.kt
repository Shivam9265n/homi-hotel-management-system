package com.example.bait2113_homi_hms.Checklist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.InventoryModel
import com.example.bait2113_homi_hms.R


class ChecklistAdapter(
        private val context: Context,
        private val checklistData: MutableList<ChecklistModel>
) : RecyclerView.Adapter<ChecklistAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checklistName: CheckBox = view.findViewById<CheckBox>(R.id.chk_checklist_inv)
    }

    var count: Int = 0

    override fun getItemCount(): Int {
        Log.i("Checklist", checklistData.size.toString())
        return checklistData.size

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.checklist_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = checklistData[position]
        holder.checklistName.text = item.prodName
        holder.checklistName.isChecked = item.isChecked

        holder.checklistName.setOnClickListener(View.OnClickListener {
            if (item.isChecked) {
                item.isChecked = false
                count--
            } else {
                item.isChecked = true
                count++
            }
        })

    }

}