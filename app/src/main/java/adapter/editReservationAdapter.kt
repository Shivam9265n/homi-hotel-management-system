package com.example.bait2113_homi_hms.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.objectModel.RevRoomList

class editReservationAdapter (
    private var context: Context,
    private var reservationList: MutableList<RevRoomList>,
    private val listener: OnItemClickListener
        ) : RecyclerView.Adapter<editReservationAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {

        val roomName: TextView = view.findViewById(R.id.roomName)
        val room_price: TextView = view.findViewById(R.id.roomPrice)
        val editRevBtn = view.findViewById<Button>(R.id.editRoom)
        val room_cat: TextView = view.findViewById(R.id.room_cat)
        val roomImage : ImageView = view.findViewById(R.id.roomImage)

        override fun onClick(v: View?) {

       }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): editReservationAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.edit_reservation_item_list, parent, false)
        val metrics = DisplayMetrics()
        context.display?.getRealMetrics(metrics)
        val devicewidth: Int = metrics.widthPixels
        val deviceheight: Int = metrics.heightPixels
        itemView.layoutParams.width = (devicewidth / 2) - 16
        return MyViewHolder(itemView)
    }

    //hold data
    override fun onBindViewHolder(holder: editReservationAdapter.MyViewHolder, position: Int) {
        val rev = reservationList[position]

        holder.roomName.text = rev.roomName
        holder.room_cat.text = rev.roomCat
        holder.room_price.text = String.format("%.2f",rev.roomPrice)
        Glide.with(context).load(rev.roomImage).into(holder.roomImage)

        holder.editRevBtn.setOnClickListener {
            listener.editReservation(position)
        }
    }

    fun onClear(){
        reservationList.clear()
    }

    override fun getItemCount(): Int {
        return reservationList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun editReservation(position: Int)
    }
}