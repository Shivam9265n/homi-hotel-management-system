package com.example.bait2113_homi_hms.Housekeeping

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.R

class HousekeepingAdapter(
        private val context: Context,
        private var housekeepingData: MutableList<HousekeepingModel>,
        var listener: OnItemClickListener
) : RecyclerView.Adapter<HousekeepingAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
        val roomNum: TextView = view.findViewById<TextView>(R.id.room_no)
        val roomType: TextView = view.findViewById<TextView>(R.id.room_type)
        val requestedDate: TextView = view.findViewById<TextView>(R.id.req_date)
        val requestedTime: TextView = view.findViewById<TextView>(R.id.req_time)
        val housekeeperName: TextView = view.findViewById<TextView>(R.id.housekeeper_name)
        val image: ImageView = view.findViewById(R.id.room_Image_housekeeping)
        val assignBtn: Button = view.findViewById(R.id.assign_btn)
        val doneBtn: Button = view.findViewById(R.id.done_btn)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun assignStaff(position: Int)
        fun doneHousekeping(position: Int)
    }

    override fun getItemCount(): Int {
        return housekeepingData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.housekeeping_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = housekeepingData[position]
        holder.roomNum.text = item.roomName
        holder.roomType.text = item.getRoomCat()
        if (item.getHousekeeper() != "") {
            holder.housekeeperName.text = item.getHousekeeper()
        } else {
            holder.housekeeperName.text = "None"
        }
        holder.requestedDate.text = item.getDateCreated()
        holder.requestedTime.text = item.getTimeCreated()
        Glide.with(context).load(item.roomImage).into(holder.image)
        holder.doneBtn.setOnClickListener {
            listener.doneHousekeping(position)
        }
        holder.assignBtn.setOnClickListener {
            if (holder.housekeeperName.text.equals("None"))
                listener.assignStaff(position)
            else
                Toast.makeText(context,
                        "The housekeeper has been assigned !!!", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClear() {
        housekeepingData.clear()
    }

    fun setHousekeepingDetail(dataHousekeeping: List<HousekeepingModel>) {
        this.housekeepingData = dataHousekeeping as MutableList<HousekeepingModel>
        notifyDataSetChanged()
    }
}