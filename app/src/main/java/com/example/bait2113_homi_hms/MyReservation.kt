package com.example.bait2113_homi_hms

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.adapter.MyReservationAdapter
import com.example.bait2113_homi_hms.objectModel.Reservation
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.*

class MyReservation  : AppCompatActivity() {

    private var purchaseList = Reservation()
    private lateinit var reservationAdapter: MyReservationAdapter

    var key = ""
    private lateinit var auth: FirebaseAuth
    lateinit var subtotal: TextView
    lateinit var additional_amt: TextView
    lateinit var total_order_amt: TextView
    lateinit var checkInDate: TextView
    lateinit var total_item: TextView
    lateinit var shipping_amt : TextView
    private var rev_item = Reservation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_reservation)

        subtotal = findViewById(R.id.subtotal_amt)
        additional_amt = findViewById(R.id.additional_amt)
        total_order_amt = findViewById(R.id.total_amt)
        checkInDate = findViewById(R.id.transaction_date)
        total_item = findViewById(R.id.total_item)
        shipping_amt = findViewById(R.id.additional_amt)

        auth = FirebaseAuth.getInstance()

        //Up button
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }

        if (intent.extras != null) {
            key = intent.getStringExtra("ReservationKey").toString()
        }
        getData()

        reservationAdapter = MyReservationAdapter(purchaseList.room_list, this)
        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP
        flexManager.flexDirection = FlexDirection.ROW
        flexManager.alignItems = AlignItems.FLEX_START

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = flexManager
        recyclerView.adapter = reservationAdapter
    }

    fun getData(){

        var item = Reservation()
        val database = FirebaseDatabase.getInstance()
        var myRef: DatabaseReference = database.getReference("Reservation/" + key + "/")

        var getData = object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                item  = snapshot.getValue(Reservation::class.java)!!

                //get total info data
                findViewById<TextView>(R.id.transaction_date).text = item.getCheckInDate()
                findViewById<TextView>(R.id.total_item).text = item.total_item.toString()

                subtotal.setText(String.format("%.2f",item.subtotal_amt))
                shipping_amt.setText(String.format("%.2f",item.additional_fees))
                total_order_amt.setText(String.format("%.2f",item.total_amt))

                //get recycler view data
                for(i in item.room_list){
                    purchaseList.room_list.add(i)
                    reservationAdapter.notifyItemInserted(purchaseList.room_list.size)
                }
            }
        }
        myRef.addValueEventListener(getData)
    }
}