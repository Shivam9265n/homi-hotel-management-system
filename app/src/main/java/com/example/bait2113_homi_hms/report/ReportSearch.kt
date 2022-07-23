package com.example.bait2113_homi_hms.report

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.payment.PaymentUtils.Companion.getCurrencyString
import com.google.firebase.database.*
import objectModel.TransactionModel


class ReportSearch : Fragment(), TransactionListAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = ReportSearch()
    }

    private var daySalesData = mutableListOf<TransactionModel>()
    private lateinit var adapter: TransactionListAdapter
    private lateinit var reportContext: Context
    private lateinit var recyclerDaySalesList: RecyclerView
    private lateinit var editTextDaySelect: EditText
    private lateinit var editTextDaySelectEnd: EditText
    private lateinit var textSalesCount: TextView
    private lateinit var textTotalSalesMoney: TextView
    private lateinit var textLabelStart: TextView
    private lateinit var textWarnLimit: TextView

    var month = 0
    var year = 0

    // count for summary
    var salesAmount: Double = 0.0

    // database search limit
    private val fireBaseLimit = 50

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Reference: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        val mView: View = inflater.inflate(R.layout.fragment_report, container, false)
        recyclerDaySalesList = mView.findViewById(R.id.recycler_day_sales_list)
        editTextDaySelect = mView.findViewById(R.id.edittext_day_select)
        editTextDaySelectEnd = mView.findViewById(R.id.edittext_day_select_end)
        textSalesCount = mView.findViewById(R.id.text_sales_count)
        textTotalSalesMoney = mView.findViewById(R.id.text_total_sales_money)
        textLabelStart = mView.findViewById(R.id.text_label_start)
        textWarnLimit = mView.findViewById(R.id.text_warn_limit)

        textWarnLimit.isVisible = true
        textWarnLimit.text = getString(R.string.limit_search_result, fireBaseLimit, fireBaseLimit)
        textLabelStart.text = getString(R.string.enter_start_keyword)
        // get once first
        getTransactionsFromDB(editTextDaySelect.text.toString())
        editTextDaySelect.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                getTransactionsFromDB(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
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

    private fun getTransactionsFromDB(startDate : String) {
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

                resetCounters()
                salesAmount = 0.00
                daySalesData.clear()

                while (iterator.hasNext()) {
                    transaction = iterator.next().getValue(TransactionModel::class.java) as TransactionModel
                    daySalesData.add(transaction)
                    salesAmount += transaction.payment_amount
                }


                if (daySalesData.size > 0) {
                    this@ReportSearch.recyclerDaySalesList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    adapter = TransactionListAdapter(reportContext, daySalesData, this@ReportSearch)
                    this@ReportSearch.recyclerDaySalesList.adapter = adapter
                    this@ReportSearch.recyclerDaySalesList.setHasFixedSize(true)
                }
                textSalesCount.text = daySalesData.size.toString()
                textTotalSalesMoney.text = getCurrencyString(salesAmount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        Log.i("startDate", startDate)
        Log.i("endDate", startDate + VERY_HIGH_UNICODE)

        // query to get single value (id)
        val query: Query = ref.orderByChild("id")
                .startAt(startDate)
                .endAt(startDate + VERY_HIGH_UNICODE)
                .limitToFirst(fireBaseLimit)
        query.addListenerForSingleValueEvent(queryValueListener)

    }

    override fun onItemClick(position: Int) {
        val transactionId = daySalesData[position].id
        passData(transactionId)
    }

    fun resetCounters() {
        salesAmount = 0.00
        textSalesCount.text = daySalesData.size.toString()
        textTotalSalesMoney.text = getCurrencyString(salesAmount)
    }

    interface OnSearchDataPass {
        fun onSearchDataPass(data: String)
    }

    lateinit var dataPasser: OnSearchDataPass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnSearchDataPass
    }

    fun passData(data: String){
        dataPasser.onSearchDataPass(data)
    }
}