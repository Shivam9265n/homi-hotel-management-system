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
import objectModel.SummaryTransactionModel
import java.text.SimpleDateFormat
import java.util.*

class ReportSummaryListAdapter(
        private val context: Context,
        private val recyclerSummarySalesList: MutableList<SummaryTransactionModel>
) : RecyclerView.Adapter<ReportSummaryListAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTransactionId: TextView = view.findViewById(R.id.text_transaction_noclick_id)
        val textTransactionAmount: TextView = view.findViewById(R.id.text_transaction_noclick_amount)
        val textTime: TextView = view.findViewById(R.id.text_transaction_noclick_time)
        // val imageNavArrow: ImageView = view.findViewById<ImageView>(R.id.image_nav_arrow)
    }

    /* generate date for below API level 26 (can't use LocalDateTime.now due to this) , override the toString part of Date */
    /* reference: https://stackoverflow.com/questions/47006254/how-to-get-current-local-date-and-time-in-kotlin */
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formattedDate = SimpleDateFormat(format, locale)
        return formattedDate.format(this)
    }

    var count: Int = 0

    override fun getItemCount(): Int {
        Log.i("Transaction", recyclerSummarySalesList.size.toString())
        return recyclerSummarySalesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.transaction_summary_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val month = recyclerSummarySalesList[position]
        holder.textTransactionId.text = ""
        holder.textTime.text = month.month
        holder.textTransactionAmount.text = PaymentUtils.getCurrencyString(month.payment_amount)
        this.count++
    }

}