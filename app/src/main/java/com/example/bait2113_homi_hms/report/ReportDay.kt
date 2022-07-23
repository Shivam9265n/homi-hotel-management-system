package com.example.bait2113_homi_hms.report

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.payment.PaymentUtils.Companion.getCurrencyString
import com.google.firebase.database.*
import objectModel.TransactionModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


class ReportDay : Fragment(), DatePickerDialog.OnDateSetListener, TransactionListAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = ReportDay()
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

    // datepicker variables
    private var day = 0
    var month = 0
    var year = 0

    private var selectDay = ""
    private var selectMonth = ""
    private var selectYear = ""

    // count for summary
    var salesAmount: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val mView: View = inflater.inflate(R.layout.fragment_report, container, false)
        recyclerDaySalesList = mView.findViewById(R.id.recycler_day_sales_list)
        editTextDaySelect = mView.findViewById(R.id.edittext_day_select)
        editTextDaySelectEnd = mView.findViewById(R.id.edittext_day_select_end)
        textSalesCount = mView.findViewById(R.id.text_sales_count)
        textTotalSalesMoney = mView.findViewById(R.id.text_total_sales_money)
        textLabelStart = mView.findViewById(R.id.text_label_start)
        // selectDate()
        var selectedDate: LocalDate

        // calendar
        editTextDaySelect.setOnClickListener {
            DatePickerDialog(requireActivity(), this, year, month, day,).show()
        }
        editTextDaySelect.isFocusable = false
        getDateCalendar()

        textLabelStart.text = getString(R.string.date_choose)

        editTextDaySelect.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                resetCounters()
                try {
                    selectedDate = LocalDate.parse(editTextDaySelect.text.toString(), DateTimeFormatter.ISO_DATE)
                    val endDate = selectedDate.plusDays(1)
                    Log.i("Starting Date", selectedDate.toString())
                    Log.i("End date", endDate.toString())

                    if (selectedDate.toString().length >= 10) {
                        daySalesData.clear()
                        getTransactionsFromDB("$selectedDate", "$endDate")
                    }
                } catch (dtp: DateTimeParseException) {
                    // no need to error check as input is limited
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        return mView
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //  https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        //  The documentation states that onActivityCreated() is the recommended place to find and store references to your views. You must clean up these stored references by setting them back to null in onDestroyView() or you will leak the Activity
        super.onActivityCreated(savedInstanceState)
        reportContext = requireContext()
    }

    private fun getTransactionsFromDB(startDate : String, endDate : String) {
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
                while (iterator.hasNext()) {
                    transaction = iterator.next().getValue(TransactionModel::class.java) as TransactionModel
                    transaction.time = transaction.time.substring(transaction.time.indexOf("T") + 1)
                    transaction.time.trim { it <= ' ' }
                    daySalesData.add(transaction)
                    salesAmount += transaction.payment_amount
                }

                if (daySalesData.size > 0) {
                    this@ReportDay.recyclerDaySalesList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    adapter = TransactionListAdapter(reportContext, daySalesData, this@ReportDay)
                    this@ReportDay.recyclerDaySalesList.adapter = adapter
                    this@ReportDay.recyclerDaySalesList.setHasFixedSize(true)
                }
                textSalesCount.text = daySalesData.size.toString()
                textTotalSalesMoney.text = getCurrencyString(salesAmount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        // query to get single value (id)
        val query: Query = ref.orderByChild("time")
                .startAt(startDate).endAt(endDate)
        query.addListenerForSingleValueEvent(queryValueListener)
    }

    override fun onItemClick(position: Int) {
        val transactionId = daySalesData[position].id
        passData(transactionId)
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectDay = dayOfMonth.toString().padStart(2, '0')
        selectMonth = month.inc().toString().padStart(2, '0')
        selectYear = year.toString().padStart(2, '0')

        editTextDaySelect.setText("$selectYear-$selectMonth-$selectDay")
    }

    private fun getDateCalendar(){
        val calendar : Calendar = Calendar.getInstance()

        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
    }

    interface OnDataPass {
        fun onDataPass(data: String)
    }

    lateinit var dataPasser: OnDataPass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    fun passData(data: String){
        dataPasser.onDataPass(data)
    }

    fun resetCounters() {
        salesAmount = 0.00
        textSalesCount.text = daySalesData.size.toString()
        textTotalSalesMoney.text = getCurrencyString(this.salesAmount)
    }
}