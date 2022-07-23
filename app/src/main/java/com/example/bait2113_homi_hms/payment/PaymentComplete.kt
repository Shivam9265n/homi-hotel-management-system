package com.example.bait2113_homi_hms.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bait2113_homi_hms.MainActivity
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.objectModel.Reservation
import com.example.bait2113_homi_hms.objectModel.ReservationList
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.example.bait2113_homi_hms.payment.PaymentUtils.Companion.getCurrencyString
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import objectModel.TransactionModel
import java.util.*

class PaymentComplete : AppCompatActivity() {

    private var reservationList: MutableList<RevRoomList> = ReservationList.getRevList()

    // unused probably remove
    var keyPosition = 0
    private lateinit var viewModel: Reservation
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    // payment use
    lateinit var guestName: String
    lateinit var phoneNumber: String
    lateinit var email: String
    lateinit var remarks: String
    lateinit var checkInDate: String
    lateinit var checkOutDate: String
    private lateinit var cardNumber: String

    private lateinit var textInvoiceNumber: TextView
    private lateinit var textReservationNumber: TextView
    private lateinit var textPaymentTime: TextView
    private lateinit var textPaymentAmount: TextView
    private lateinit var textPaymentMethod: TextView
    private lateinit var textCardNumber: TextView

    // reservation use
    private var additionalFees: Double = 0.00
    var subtotalAmt: Double = 0.00
    var totalAmt: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_complete)
        reservationList= ReservationList.getRevList()

        // get views
        textInvoiceNumber = findViewById(R.id.text_invoice_number)
        textReservationNumber = findViewById(R.id.text_reservation_number)
        textPaymentTime = findViewById(R.id.text_subtotal_amount)
        textPaymentAmount = findViewById(R.id.text_payment_amount)
        textPaymentMethod = findViewById(R.id.text_payment_method)
        textCardNumber = findViewById(R.id.text_card_number)

        if (intent != null) {
            //Log.i("Guest Name", guestName.toString())
            guestName= intent.getStringExtra("guestName").toString()
            phoneNumber = intent.getStringExtra("phoneNumber").toString()
            email = intent.getStringExtra("email").toString()
            remarks = intent.getStringExtra("remarks").toString()
            checkInDate = intent.getStringExtra("checkInDate").toString()
            checkOutDate = intent.getStringExtra("checkOutDate").toString()

            if (intent.getStringExtra("paymentMethod").toString() == "cash") {
                cardNumber = ""
            }
        }

        addReservationDb()
    }

    private fun addTransactionIntoDb() {
        val transactionReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Transaction")
        val id: String? = transactionReference.push().key
        val transaction = TransactionModel(
                id.toString(),
                PaymentUtils.dateToString(Calendar.getInstance().time, "yyyy-MM-dd'T'HH:mm:ss"),
                intent.getStringExtra("paymentMethod").toString(),
                intent.getStringExtra("cardNumber").toString(),
                intent.getStringExtra("guestName").toString(),
                intent.getStringExtra("taxAmount").toString().toDouble(),
                intent.getStringExtra("paymentAmount").toString().toDouble(),
                textReservationNumber.text.toString())
        if (id != null) {
            transactionReference.child(id).setValue(transaction)
            Toast.makeText(this, "Transaction success!", Toast.LENGTH_SHORT).show()

            // set values on interface
            setPaymentValues(transaction)
        }
    }

    private fun setPaymentValues(transaction: TransactionModel) {
        textInvoiceNumber.text = transaction.id
        textPaymentTime.text = transaction.time
        textPaymentAmount.text = getCurrencyString(transaction.payment_amount)
        textPaymentMethod.text = transaction.payment_method.capitalize(Locale.ROOT)
        if (transaction.payment_method.toLowerCase(Locale.ROOT) == "card") {
            textCardNumber.text = transaction.card_number
        }
        else {
            textCardNumber.text = "N/A"
        }
    }

    fun completePayment(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun addReservationDb(){
        //write checkout details into database
        val database = FirebaseDatabase.getInstance()
        var myRef1: DatabaseReference = database.getReference("Reservation/")
        val itemList : MutableList<RevRoomList> = mutableListOf()
        val roomList = ReservationList.getRevList()
        val totalItem = ReservationList.getRevListSize()
        val floor = 1

        for(room in roomList){
            Log.i("Complete", room.roomStatus)
            if(room.roomStatus.equals("Reserved")){
                val convertToItem = RevRoomList(
                        roomID = room.roomID,
                        roomName = room.roomName,
                        roomCat = room.roomCat,
                        roomImage = room.roomImage,
                        roomPrice = room.roomPrice,
                        roomStatus = room.roomStatus,
                        bed_Add_On = room.bed_Add_On,
                        roomQty = 1,
                        floor = room.floor
                )
                itemList.add(convertToItem)
            }
        }
        val pushkey: String?= myRef1.push().key
        val status = "Reserved"

        totalAmt = ReservationList.calcTotal()
        subtotalAmt = ReservationList.calcSubTotal()
        additionalFees = totalAmt-subtotalAmt
        //write the details of checkout into database
        myRef1 = database.getReference("Reservation/" + pushkey + "/")

        var details = Reservation(
                "${guestName}",
                "${pushkey}",
                "${status}",
                "${remarks}",
                "${checkInDate}",
                "${checkOutDate}",
                "${email}",
                additionalFees,
                subtotalAmt,
                totalAmt,
                totalItem,
                "${phoneNumber}",
                itemList
        )
        myRef1.setValue(details)

        //Set unselected item as available\
        val unselectedRoomList = ReservationList.getRevList()
        val database1 = FirebaseDatabase.getInstance()

        val items: List<String> = checkInDate.split("-")
        var startYear = items.get(0).toInt()
        val startMonth = items.get(1).toInt()
        val startDay = items.get(2).toInt()

        val items1: List<String> = checkOutDate.split("-")
        var endYear = items1.get(0).toInt()
        var endMonth = items1.get(1).toInt()
        val endDay = items1.get(2).toInt()

        var startDay1 = startDay
        var endDay1 = endDay

        for(i in unselectedRoomList){
            startDay1 = startDay
            if(i.roomStatus.equals("Unselected")){
                while(startDay1 <= endDay){
                    val ref: DatabaseReference = database1.getReference("Date/2021/0" + startMonth.toString() + "/"
                            + (startDay1).toString() + "/" + i.roomID + "/")
                    ref?.child("roomStatus")?.setValue("Available")
                    startDay1+=1
                }
            }
        }

        // write push key into complete screen
        textReservationNumber.text = details.rev_id
        // write into transactions
        addTransactionIntoDb()

        // clear
        ReservationList.clear()
    }

    override fun onBackPressed() {
        val backIntent = Intent()
        backIntent.setClass(applicationContext, MainActivity::class.java)
        backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(backIntent)
        finish()
    }
}