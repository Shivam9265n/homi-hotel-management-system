package com.example.bait2113_homi_hms.report

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.payment.PaymentUtils.Companion.getCurrencyString
import com.google.firebase.database.*
import objectModel.SummaryTransactionModel
import objectModel.TransactionModel


class ReportMonth : Fragment() {

    companion object {
        fun newInstance() = ReportMonth()
    }

    private var daySalesData = mutableListOf<TransactionModel>()
    private var monthSalesData = mutableListOf<SummaryTransactionModel>()
    private lateinit var adapter: ReportSummaryListAdapter
    private lateinit var reportContext: Context
    private lateinit var recyclerDaySalesList: RecyclerView
    private lateinit var editTextMonthSelect: EditText
    private lateinit var textSalesCount: TextView
    private lateinit var textTotalSalesMoney: TextView
    private lateinit var textLabelStart: TextView

    var month = 0
    var year = 0

    // count for summary
    var salesAmount: Double = 0.0
    var salesCount: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Reference: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        val mView: View = inflater.inflate(R.layout.fragment_report, container, false)
        recyclerDaySalesList = mView.findViewById(R.id.recycler_day_sales_list)
        editTextMonthSelect = mView.findViewById(R.id.edittext_day_select)
        textSalesCount = mView.findViewById(R.id.text_sales_count)
        textTotalSalesMoney = mView.findViewById(R.id.text_total_sales_money)
        textLabelStart = mView.findViewById(R.id.text_label_start)
        // selectDate

        textLabelStart.text = getString(R.string.enter_year)
        editTextMonthSelect.inputType = InputType.TYPE_CLASS_DATETIME

        val customisedErrorIcon = ResourcesCompat.getDrawable(resources, R.drawable.error_icon_display, null)
        customisedErrorIcon?.setBounds(
                0, 0,
                customisedErrorIcon.intrinsicWidth,
                customisedErrorIcon.intrinsicHeight
        )

        editTextMonthSelect.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                try {
                    if (editTextMonthSelect.text.isNotEmpty()) {
                        val yearStart = editTextMonthSelect.text.toString().toInt()
                        if (yearStart.toString().length == 4) {
                            monthSalesData.clear()
                            resetCounters()
                            for (i in 1..12) {
                                val monthString: String = i.toString().padStart(2, '0')
                                getTransactionsFromDB("$yearStart-$monthString-00", "$yearStart-$monthString-32", "$yearStart-$monthString")
                            }
                        } else {
                            editTextMonthSelect.setError("Enter 4 numbers only", customisedErrorIcon)
                        }
                    }
                } catch (nfe: NumberFormatException) {
                    editTextMonthSelect.setError("Enter 4 numbers only", customisedErrorIcon)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        //  The documentation states that onActivityCreated() is the recommended place to find and store references to your views. You must clean up these stored references by setting them back to null in onDestroyView() or you will leak the Activity
        super.onActivityCreated(savedInstanceState)
        reportContext = requireContext()
    }

    private fun getTransactionsFromDB(startDate : String, endDate : String, title: String) {
        // model
        var transaction: TransactionModel
        // get db reference
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Transaction")
        // controls

        // query value listener
        val queryValueListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snapshotIterator = dataSnapshot.children
                val iterator: Iterator<DataSnapshot> = snapshotIterator.iterator()
                val monthTransaction = SummaryTransactionModel(title)

                while (iterator.hasNext()) {
                    transaction = iterator.next().getValue(TransactionModel::class.java) as TransactionModel
                    daySalesData.add(transaction)
                    monthTransaction.payment_amount += transaction.payment_amount
                    salesCount += 1
                    salesAmount += transaction.payment_amount
                }
                monthSalesData.add(monthTransaction)
                if (monthSalesData.count() >= 12) {
                    updateFragmentInfo()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        // query to get single value (id)
        val query: Query = ref.orderByChild("time")
                .startAt(startDate).endAt(endDate)
        query.addListenerForSingleValueEvent(queryValueListener)

    }

    private fun updateFragmentInfo() {
        // var recyclerDaySalesList: RecyclerView? = view?.findViewById<RecyclerView>(R.id.recycler_day_sales_list)
        this@ReportMonth.recyclerDaySalesList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = ReportSummaryListAdapter(reportContext, monthSalesData.sortedBy { it.month } as MutableList<SummaryTransactionModel>)
        this@ReportMonth.recyclerDaySalesList.adapter = adapter
        this@ReportMonth.recyclerDaySalesList.setHasFixedSize(true)
        textSalesCount.text = salesCount.toString()
        textTotalSalesMoney.text = getCurrencyString(salesAmount)
    }

    fun resetCounters() {
        salesAmount = 0.00
        salesCount = 0
    }
}