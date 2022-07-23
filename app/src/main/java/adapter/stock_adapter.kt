package adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.InventoryModel
import com.example.bait2113_homi_hms.R
import kotlinx.android.synthetic.main.stock_list_item.*

class stock_adapter(
        context: Context,
        private var stockList: MutableList<InventoryModel>,
        private val listener: stock_adapter.OnItemClickListener
) : RecyclerView.Adapter<stock_adapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        val qty: TextView = view.findViewById(R.id.qty)
        val prodName: TextView = view.findViewById(R.id.prodName)
        val edit: ImageView = view.findViewById(R.id.edit)
        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                Log.i("position", position.toString())
                listener.onItemClick(position)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.stock_list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stock = stockList[position]

        holder.qty.setText(stock.qty.toString())
        holder.prodName.setText(stock.prodName)
        holder.edit.setOnClickListener {
            listener.editStock(position)
        }
    }

    override fun getItemCount(): Int {
        return stockList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun editStock(position: Int)
    }
}