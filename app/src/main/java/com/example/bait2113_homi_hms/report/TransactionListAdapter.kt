package com.example.bait2113_homi_hms.report

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.payment.PaymentUtils
import objectModel.TransactionModel
import java.text.SimpleDateFormat
import java.util.*

class TransactionListAdapter(
        private val context: Context,
        private val recyclerDaySalesList: MutableList<TransactionModel>,
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<TransactionListAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
    View.OnClickListener {
        val textTransactionId: TextView = view.findViewById(R.id.text_transaction_id)
        val textTransactionAmount: TextView = view.findViewById(R.id.text_transaction_amount)
        val textTime: TextView = view.findViewById(R.id.text_transaction_time)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    /* generate date for below API level 26 (can't use LocalDateTime.now due to this) , override the toString part of Date */
    /* reference: https://stackoverflow.com/questions/47006254/how-to-get-current-local-date-and-time-in-kotlin */
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formattedDate = SimpleDateFormat(format, locale)
        return formattedDate.format(this)
    }

    var count: Int = 0

    override fun getItemCount(): Int {
        Log.i("Transaction", recyclerDaySalesList.size.toString())
        return recyclerDaySalesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val transaction = recyclerDaySalesList[position]
        holder.textTransactionId.text = transaction.id
        holder.textTransactionAmount.text = PaymentUtils.getCurrencyString(transaction.payment_amount)
        holder.textTime.text = transaction.time
        this.count++
    }

}