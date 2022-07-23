package com.example.bait2113_homi_hms.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.objectModel.RevRoomList

class ItemAdapter(
        private var context: Context,
        private val dataset: MutableList<RevRoomList> = mutableListOf(),
        private val listener: OnItemClickListener,
        private var listFiltered: MutableList<RevRoomList> = mutableListOf()
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(), Filterable {

    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val title: TextView = view.findViewById(R.id.item_title)
        val roomStatusIcon: ImageView = view.findViewById(R.id.stock_status_icon)
        val stockStatus: TextView = view.findViewById(R.id.stockStatus)
        val roomCat: TextView = view.findViewById(R.id.roomCat)
        val addtocartBtn: Button = view.findViewById(R.id.addToCartButton)

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
        fun addRevStoredList(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        val metrics = DisplayMetrics()
        context.display?.getRealMetrics(metrics)
        val devicewidth: Int = metrics.widthPixels
        val deviceheight: Int = metrics.heightPixels
        adapterLayout.layoutParams.width = (devicewidth / 2) - 16

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = listFiltered[position]
        holder.title.text = item.roomName
        Glide.with(context).load(item.roomImage).into(holder.imageView)

        holder.roomStatusIcon.setImageResource(R.drawable.instock)
        holder.stockStatus.text = context.getString(R.string.available)

        holder.roomCat.text = item.roomCat.toString()

        val metrics = DisplayMetrics()
        context.display?.getRealMetrics(metrics)
        val devicewidth: Int = metrics.widthPixels
        val deviceheight: Int = metrics.heightPixels
        holder.imageView.layoutParams.height = (deviceheight / 4) - 16
        holder.addtocartBtn.setOnClickListener {
            listener.addRevStoredList(position)
        }
    }

    fun onClear(){
        listFiltered.clear()
    }

    override fun getItemCount() = listFiltered.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    listFiltered = dataset

                } else {
                    var filteredList: ArrayList<RevRoomList> = arrayListOf()
                    for (rev: RevRoomList in dataset) {

                        if (rev.roomCat.toLowerCase().contains(charString.toLowerCase())) {
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
                listFiltered = results!!.values as ArrayList<RevRoomList>
                notifyDataSetChanged()
            }

        }
    }
}