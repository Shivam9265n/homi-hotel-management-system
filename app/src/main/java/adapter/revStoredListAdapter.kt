package com.example.bait2113_homi_hms.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.ButtonClickedListener
import com.example.bait2113_homi_hms.objectModel.RevRoomList


class revStoredListAdapter(
    // variable
        private var revRoomListList: List<RevRoomList>,
        private var context: Context,
        private val buttonclick : ButtonClickedListener,
    ) : RecyclerView.Adapter<revStoredListAdapter.ItemViewHolder>() {

    var isSelectedAll: Boolean = false

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val roomImage: ImageView = view.findViewById(R.id.roomImage)
        val roomName: TextView = view.findViewById(R.id.roomName)
        val roomCat: TextView = view.findViewById(R.id.roomCat)
        val price: TextView = view.findViewById(R.id.roomPrice)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox_in_list)
        val bed_add_on: TextView = view.findViewById<TextView>(R.id.bed_add_on)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): revStoredListAdapter.ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservation_room_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = revRoomListList[position]
        Glide.with(context).load(item.roomImage).into(holder.roomImage)

        if(item.bed_Add_On == 1){
            holder.price.setText("RM " + String.format("%.2f", item.roomPrice + 100))
        }
        else{
            holder.bed_add_on.setText("");
            holder.price.setText("RM " + String.format("%.2f", item.roomPrice))
        }
        holder.roomName.setText(item.roomName)
        holder.roomCat.setText(item.roomCat)
        if (!isSelectedAll){
            holder.checkbox.setChecked(false)
        }
        else {
            holder.checkbox.setChecked(true)
        }

        holder.checkbox.setOnClickListener{
            if(holder.checkbox.isChecked){
                buttonclick.getCheckedItem(item,true, position)
                Log.i("revRoomAdapter", position.toString())
            }else{
                buttonclick.getCheckedItem(item,false, position)
            }
        }
    }

    fun selectAll() {
        isSelectedAll = true;
        notifyDataSetChanged();
    }

    fun unselectAll() {
        isSelectedAll = false;
        notifyDataSetChanged();
    }

    override fun getItemCount() = revRoomListList.size
    }
