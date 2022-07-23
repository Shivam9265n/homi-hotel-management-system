package com.example.bait2113_homi_hms.report

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.payment.PaymentUtils
import com.google.firebase.database.*
import objectModel.SummaryTransactionModel
import objectModel.TransactionModel
import kotlin.math.abs
import kotlin.math.log10


class ReportYear : Fragment() {

    companion object {
        fun newInstance() = ReportYear()
    }

    private var daySalesData = mutableListOf<TransactionModel>()
    private var monthSalesData = mutableListOf<SummaryTransactionModel>()
    private lateinit var adapter: ReportSummaryListAdapter
    private lateinit var reportContext: Context
    private lateinit var recyclerDaySalesList: RecyclerView
    private lateinit var editTextDaySelect: EditText
    private lateinit var editTextDaySelectEnd: EditText
    private lateinit var textSalesCount: TextView
    private lateinit var textTotalSalesMoney: TextView
    private lateinit var textLabelStart: TextView
    private lateinit var textLabelEnd: TextView

    var month = 0
    var year = 0

    private var yearStart = 0
    private var yearEnd = 0

    // count for summary
    var salesAmount: Double = 0.0
    var salesCount: Int = 0



    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //Reference: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        val mView: View = inflater.inflate(R.layout.fragment_report, container, false)
        recyclerDaySalesList = mView.findViewById(R.id.recycler_day_sales_list)
        editTextDaySelect = mView.findViewById(R.id.edittext_day_select)
        editTextDaySelectEnd = mView.findViewById(R.id.edittext_day_select_end)
        textSalesCount = mView.findViewById(R.id.text_sales_count)
        textTotalSalesMoney = mView.findViewById(R.id.text_total_sales_money)
        textLabelStart = mView.findViewById(R.id.text_label_start)
        textLabelEnd = mView.findViewById(R.id.text_label_end)

        // set start & end labels
        textLabelStart.text = getString(R.string.start_year)
        textLabelEnd.text = getString(R.string.end_year)
        textLabelEnd.isVisible = true

        editTextDaySelect.inputType = InputType.TYPE_CLASS_DATETIME
        editTextDaySelectEnd.inputType = InputType.TYPE_CLASS_DATETIME
        editTextDaySelectEnd.visibility = View.VISIBLE
        editTextDaySelectEnd.isFocusable = true

        editTextDaySelect.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                yearChangeHandler()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        editTextDaySelectEnd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                yearChangeHandler()
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

    fun yearChangeHandler() {
        val customisedErrorIcon = ResourcesCompat.getDrawable(resources, R.drawable.error_icon_display, null)
        customisedErrorIcon?.setBounds(
                0, 0,
                customisedErrorIcon.intrinsicWidth,
                customisedErrorIcon.intrinsicHeight
        )

        fun parseStartDate(): Boolean {
            try {
                if (editTextDaySelect.text.isBlank()) {
                    return false
                }
                yearStart = editTextDaySelect.text.toString().toInt()
                if (yearStart.length() != 4) {
                    throw java.lang.NumberFormatException()
                } else {
                    return true
                }
            } catch (nfe: NumberFormatException) {
                editTextDaySelect.setError("Enter 4 numbers only", customisedErrorIcon)
                return false
            } finally {
                editTextDaySelectEnd.setError(null, null)
            }
        }

        fun parseEndDate(): Boolean {
            try {
                if (editTextDaySelectEnd.text.isBlank()) {
                    return false
                }
                yearEnd = editTextDaySelectEnd.text.toString().toInt()
                if (yearEnd.length() != 4) {
                    throw java.lang.NumberFormatException()
                } else {
                    return true
                }
            } catch (nfe: NumberFormatException) {
                editTextDaySelectEnd.setError("Enter 4 numbers only", customisedErrorIcon)
                return false
            } finally {
                editTextDaySelect.setError(null, null)
            }
        }


        fun getData() = try {
            val maxYearDiff = 10
            val yearDifference = yearEnd - yearStart
            when {
                yearDifference < 0 -> {
                    editTextDaySelect.setError("End year less than start year", customisedErrorIcon)
                    editTextDaySelectEnd.setError("End year less than start year", customisedErrorIcon)
                }
                yearDifference > maxYearDiff -> {
                    editTextDaySelect.setError("Maximum $maxYearDiff year difference", customisedErrorIcon)
                    editTextDaySelectEnd.setError("Maximum $maxYearDiff year difference", customisedErrorIcon)
                }
                else -> {
                    editTextDaySelect.setError(null, null)
                    editTextDaySelectEnd.setError(null, null)
                    monthSalesData.clear()
                    resetCounters()
                    for (i in yearStart..yearEnd) {
                        val j = i + 1 // account for database lexicographic search
                        getTransactionsFromDB("$i", "$j", i.toString())
                    }
                }
            }
        } catch (nfe: NumberFormatException) {
            editTextDaySelectEnd.setError("Enter 4 numbers only", customisedErrorIcon)
        }

        if (parseStartDate() && parseEndDate()) {
            getData()
        }

    }

    // reference: https://stackoverflow.com/questions/42950812/count-number-of-digits-in-kotlin
    private fun Int.length() = when(this) {
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }


    private fun getTransactionsFromDB(startDate: String, endDate: String, title: String) {
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
                }
                monthSalesData.add(monthTransaction)
                salesAmount += monthTransaction.payment_amount

                updateFragmentInfo()
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
        this@ReportYear.recyclerDaySalesList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = ReportSummaryListAdapter(reportContext, monthSalesData.sortedBy { it.month } as MutableList<SummaryTransactionModel>)
        this@ReportYear.recyclerDaySalesList.adapter = adapter
        this@ReportYear.recyclerDaySalesList.setHasFixedSize(true)
        textSalesCount.text = salesCount.toString()
        textTotalSalesMoney.text = PaymentUtils.getCurrencyString(salesAmount)
    }

    private fun resetCounters() {
        salesAmount = 0.00
        salesCount = 0
    }

}
