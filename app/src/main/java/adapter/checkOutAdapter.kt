package com.example.bait2113_homi_hms.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.objectModel.Reservation

class checkOutAdapter (private var context: Context,
                       private var reservationList: MutableList<Reservation>,
                       private val listener: OnItemClickListener,
                       private var listFiltered: MutableList<Reservation>

    ) : RecyclerView.Adapter<checkOutAdapter.MyViewHolder>(), Filterable {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view),
                View.OnClickListener {

            val checkInDate: TextView = view.findViewById(R.id.checkInDate)
            val guestName: TextView = view.findViewById(R.id.guestName)
            val checkOutBtn = view.findViewById<Button>(R.id.checkOutBtn)
            val item_image = view.findViewById<ImageView>(R.id.item_image)
            val checkOutDate : TextView = view.findViewById(R.id.checkOutDate)

            init {
            }
            override fun onClick(v: View?) {

            }
        }

        @NonNull
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.check_out_list_item, parent, false)
            return MyViewHolder(itemView)
        }

        //hold data
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val history = listFiltered[position]

            holder.checkInDate.text = history.getCheckInDate()
            holder.checkOutDate.text = history.getCheckOutDate()
            holder.guestName.text = history.guestName
            Glide.with(context).load(history.room_list[0].roomImage).into(holder.item_image)

            holder.checkOutBtn.setOnClickListener {
                listener.checkOut(position)
            }
        }

        fun deleteRecyclerView(position: Int){
            reservationList.clear()
            notifyItemRemoved(position);
        }

        override fun getItemCount(): Int {
            return reservationList.size
        }

        interface OnItemClickListener {
            fun checkOut(position: Int)
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    var charString: String = constraint.toString()
                    if (charString.isEmpty()) {
                        listFiltered = reservationList

                    } else {
                        var filteredList: MutableList<Reservation> = mutableListOf()
                        for (rev: Reservation in reservationList) {

                            if (rev.guestName.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(rev)
                            }
                        }
                        listFiltered = filteredList
                    }
                    var filterResults: FilterResults = FilterResults()
                    filterResults.values = listFiltered
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    listFiltered = results!!.values as MutableList<Reservation>
                    notifyDataSetChanged()
                }
            }
        }
    }