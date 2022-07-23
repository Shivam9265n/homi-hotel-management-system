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

class ReservationAdapter (
    private var context: Context,
    private var reservationList: MutableList<Reservation> = mutableListOf(),
    private val listener: OnItemClickListener,
    private var revFilteredList: MutableList<Reservation> = mutableListOf()

) : RecyclerView.Adapter<ReservationAdapter.MyViewHolder>(), Filterable {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {

        val checkInDate: TextView = view.findViewById(R.id.checkInDate)
        val guestName: TextView = view.findViewById(R.id.guestName)
        val editRevBtn = view.findViewById<Button>(R.id.editRevBtn)
        val deleteRevBtn = view.findViewById<Button>(R.id.deleteRevBtn)
        val roomImage = view.findViewById<ImageView>(R.id.roomImage)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                Log.i("position", position.toString())
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun editReservation(position: Int)
        fun deleteReservation(position: Int)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_reservation_item_list, parent, false)
        val metrics = DisplayMetrics()
        context.display?.getRealMetrics(metrics)
        val devicewidth: Int = metrics.widthPixels
        val deviceheight: Int = metrics.heightPixels
        itemView.layoutParams.width = (devicewidth / 2) - 5
        return MyViewHolder(itemView)
    }

    //hold data
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = reservationList[position]

        holder.checkInDate.text = history.getCheckInDate()
        holder.guestName.text = history.guestName
        Glide.with(context).load(history.room_list[0].roomImage).into(holder.roomImage)

        holder.editRevBtn.setOnClickListener {
            listener.editReservation(position)
        }
        holder.deleteRevBtn.setOnClickListener {
            listener.deleteReservation(position)
        }
    }

    fun onClear(){
        reservationList.clear()
        revFilteredList.clear()
    }

    override fun getItemCount() = revFilteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    revFilteredList = reservationList

                } else {
                    var filteredList: MutableList<Reservation> = mutableListOf()
                    for (rev: Reservation in reservationList) {

                        if (rev.guestName.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(rev)
                        }
                    }
                    revFilteredList = filteredList
                }
                var filterResults: FilterResults = FilterResults()
                filterResults.values = revFilteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                revFilteredList = results!!.values as MutableList<Reservation>
                notifyDataSetChanged()
            }
        }
    }
}