package com.example.bait2113_homi_hms.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.InventoryDetailsActivity
import com.example.bait2113_homi_hms.InventoryModel
import com.example.bait2113_homi_hms.R

class RecyclerAdapter(private val context: Context, val stockList: MutableList<InventoryModel>, private val listener: RecyclerAdapter.OnItemClickListener): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var stock_name: TextView = itemView.findViewById(R.id.stockName)
        var qty: TextView = itemView.findViewById(R.id.qty)
        var btn: ImageView = itemView.findViewById(R.id.edit)
        init {

            itemView.setOnClickListener {
                var position: Int = getAdapterPosition()
                val context = itemView.context
                val intent = Intent(context, InventoryDetailsActivity::class.java).apply {
                    putExtra("NUMBER" , stockList[position].inventoryId)
                }
                context.startActivity(intent)
            }
        }
    }
    interface OnItemClickListener {
        fun updateQuantity(position:Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): RecyclerAdapter.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stock_list2, parent, false)
        Log.i("Stock_name", "Enter RecyclerAdapter")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val stock : InventoryModel = stockList[i]
        Log.i("Stock_size", stockList.size.toString())
        viewHolder.stock_name.setText(stock.prodName)
        viewHolder.qty.setText(stock.qty.toString())
        viewHolder.btn.setOnClickListener{
            listener.updateQuantity(i)
        }
    }

    override fun getItemCount(): Int {
        return stockList.size
    }
    fun onClear(){
        stockList.clear()
    }

}