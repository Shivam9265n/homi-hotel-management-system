package com.example.bait2113_homi_hms.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.objectModel.Reservation
import com.google.firebase.database.*
import objectModel.TransactionModel
import java.util.*

class InvoiceFragment : Fragment() {
    private val parameter1 = "param1"
    private var transactionId: String? = null
    private lateinit var reservation: Reservation
    private lateinit var transaction: TransactionModel
    private lateinit var textInvoiceReservationDate: TextView
    private lateinit var textSubtotalAmount: TextView
    private lateinit var textInvoiceTaxAmount: TextView
    private lateinit var textInvoiceTotalAmt: TextView
    private lateinit var textInvoiceNo: TextView
    private lateinit var textCustName: TextView
    private lateinit var textContactNo: TextView
    private lateinit var textPaymentMethod: TextView
    private lateinit var textPaymentTime: TextView
    private lateinit var textReservationNo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionId = it.getString(parameter1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_invoice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textInvoiceReservationDate = view.findViewById(R.id.text_invoice_reservation_date)
        textSubtotalAmount = view.findViewById(R.id.text_subtotal_amount)
        textInvoiceTaxAmount = view.findViewById(R.id.text_invoice_tax_amount)
        textInvoiceTotalAmt = view.findViewById(R.id.text_invoice_total_amt)
        textInvoiceNo = view.findViewById(R.id.text_invoice_no)
        textCustName = view.findViewById(R.id.text_invoice_cust_name)
        textContactNo = view.findViewById(R.id.text_invoice_contact_no)
        textPaymentTime = view.findViewById(R.id.text_invoice_payment_time)
        textPaymentMethod = view.findViewById(R.id.text_invoice_payment_method)
        textReservationNo = view.findViewById(R.id.text_reservation_no)

        getTransactionFromDB(transactionId.toString())
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            InvoiceFragment().apply {
                arguments = Bundle().apply {
                    putString(parameter1, param1)
                }
            }
    }

    private fun getTransactionFromDB(id: String) {
        // model
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
                    setInvoiceValues()
                    getReservationFromDB(transaction.reservation_id)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        // query to get single value (id)
        val query: Query = ref.orderByChild("id").equalTo(id)
        query.addListenerForSingleValueEvent(queryValueListener)
    }

    private fun getReservationFromDB(id: String) {
        // model
        // get db reference
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Reservation")
        // controls

        // query value listener
        val queryValueListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snapshotIterator = dataSnapshot.children
                val iterator: Iterator<DataSnapshot> = snapshotIterator.iterator()
                while (iterator.hasNext()) {
                    reservation = iterator.next().getValue(Reservation::class.java) as Reservation
                    setReservationValues()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        // query to get single value (id)
        val query: Query = ref.orderByChild("rev_id").equalTo(id)
        query.addListenerForSingleValueEvent(queryValueListener)
    }

    private fun setInvoiceValues() {
        textInvoiceReservationDate.text = transaction.time
        textInvoiceTotalAmt.text = transaction.payment_amount.toString()
        textInvoiceNo.text = transaction.id
        textSubtotalAmount.text = (transaction.payment_amount - transaction.tax_amount).toString()
        textInvoiceTaxAmount.text = transaction.tax_amount.toString()
        textPaymentMethod.text = transaction.payment_method.toUpperCase(Locale.ROOT)
        textPaymentTime.text = transaction.time
        textReservationNo.text = transaction.reservation_id
    }

    private fun setReservationValues() {
        textCustName.text = reservation.guestName
        textContactNo.text = reservation.Contact
    }

}