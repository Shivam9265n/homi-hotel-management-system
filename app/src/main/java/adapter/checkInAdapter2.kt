package com.example.bait2113_homi_hms.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.objectModel.RevRoomList

internal class checkInAdapter2(
    private var purchaseList: List<RevRoomList>,
    private var context: Context
) : RecyclerView.Adapter<checkInAdapter2.MyViewHolder>() {
    internal inner class MyViewHolder(view: View) : ViewHolder(view),
            View.OnClickListener {

        var room_pic: ImageView = view.findViewById(R.id.room_pic)
        var room_name: TextView = view.findViewById(R.id.room_name)
        var room_qty: TextView = view.findViewById(R.id.room_qty)
        var room_price: TextView = view.findViewById(R.id.room_price)


        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.check_in_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    //hold data
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val purchase = purchaseList[position]
        Log.i("adapter",purchase.toString() + " " + purchaseList)
        Glide.with(context).load(purchase.roomImage).into(holder.room_pic)
        holder.room_name.text = purchase.roomName
        holder.room_qty.text = purchase.roomQty.toString()
        holder.room_price.text = String.format("%.2f",purchase.roomPrice)
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }

}