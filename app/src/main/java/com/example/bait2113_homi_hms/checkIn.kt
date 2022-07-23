package com.example.bait2113_homi_hms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.adapter.checkInAdapter2
import com.example.bait2113_homi_hms.objectModel.CheckInModel
import com.example.bait2113_homi_hms.objectModel.Reservation
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class checkIn : AppCompatActivity() {
    private val reservationList: MutableList<Reservation> = mutableListOf()
    private var purchaseList = Reservation()
    private lateinit var checkInAdapter2: checkInAdapter2
    var key1 = ""
    var key = ""
    var keyPosition = 0
    private lateinit var auth: FirebaseAuth
    lateinit var total_order_amt: TextView
    lateinit var checkInDate: TextView
    lateinit var checkOutDate: TextView
    lateinit var guestName: TextView
    lateinit var total_item: TextView
    lateinit var status: TextView
    lateinit var checkInBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_in)
        total_order_amt = findViewById(R.id.total_order_amt_text)
        checkInDate = findViewById(R.id.checkInDate)
        checkOutDate = findViewById(R.id.checkOutDate)
        guestName = findViewById(R.id.guestName)
        total_item = findViewById(R.id.total_item)
        status = findViewById(R.id.status)
        checkInBtn = findViewById(R.id.checkInBtn)

        auth = FirebaseAuth.getInstance()

        if (intent.extras != null) {
            keyPosition = intent.getIntExtra("keyPosition", 0)
            key = intent.getStringExtra("key").toString()
            Log.i("Rev Key", key)
        }
        getData()

        checkInAdapter2 = checkInAdapter2(purchaseList.room_list, this)
        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP
        flexManager.flexDirection = FlexDirection.ROW
        flexManager.alignItems = AlignItems.FLEX_START

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = flexManager
        recyclerView.adapter = checkInAdapter2
        backTo()
    }

    fun getData(){
        val database1 = FirebaseDatabase.getInstance()
        var myRef1 = database1.getReference("Reservation/" + key1 + "/")

        // Read from the database
        myRef1.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot : DataSnapshot) {

                for ( childDataSnapshot : DataSnapshot in dataSnapshot.getChildren()) {
                    myRef1 = database1.getReference("Reservation/" + key1 + "/" + childDataSnapshot.getKey().toString() + "/")
                    childDataSnapshot.getValue(Reservation::class.java)?.let {
                        if(it.getStatus().equals("Reserved")){
                            reservationList.add(it)
                        }
                    }
                    checkInAdapter2.notifyItemInserted(reservationList.size)
                    //Log.i("data",purchaseList.size.toString())
                }
            }

            override  fun onCancelled(databaseError : DatabaseError) {

            }
        });

        var item = Reservation()
        val database = FirebaseDatabase.getInstance()
        var myRef: DatabaseReference = database.getReference("Reservation/" + key + "/")
        Log.i("string",key )

        var getData = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                item  = snapshot.getValue(Reservation::class.java)!!

                //get total info data
                findViewById<TextView>(R.id.guestName).text = item.guestName
                findViewById<TextView>(R.id.checkInDate).text = item.getCheckInDate()
                findViewById<TextView>(R.id.checkOutDate).text = item.getCheckOutDate()
                findViewById<TextView>(R.id.total_item).text = item.total_item.toString()
                findViewById<TextView>(R.id.status).text = item.getStatus()
                val total_order_amt : TextView = findViewById(R.id.total_order_amt)
                total_order_amt.setText(String.format("%.2f",item.total_amt))

                Log.i("string",String.format("%.2f",item.subtotal_amt) )

                //get recycler view data
                for(i in item.room_list){
                    purchaseList.room_list.add(i)
                    checkInAdapter2.notifyItemInserted(purchaseList.room_list.size)
                }
                checkInBtn.setOnClickListener {
                    checkIn()
                }
            }
        }
        myRef.addValueEventListener(getData)

    }

    fun backTo() {
        val back: ImageView = findViewById(R.id.arrow_back_icon1)
        back.setOnClickListener {
            onBackPressed()
        }
    }

    fun checkIn(){
        //write checkIn details into database
        val database = FirebaseDatabase.getInstance()
        var myRef: DatabaseReference = database.getReference("CheckIn/")
        var pushkey: String?= myRef.push().key
        val itemList : MutableList<RevRoomList> = mutableListOf()

        for(room in reservationList[keyPosition].room_list){
            var convertToItem = RevRoomList(
                    room.roomID,
                    room.roomName,
                    room.roomCat,
                    room.roomImage,
                    room.roomPrice,
                    room.roomStatus,
                    room.bed_Add_On,
                    1,
                    1
            )
            itemList.add(convertToItem)
        }

        val checkInDate = reservationList[keyPosition].getCheckInDate()
        val checkOutDate = reservationList[keyPosition].getCheckOutDate()
        val status = "CheckIn"
        val guestName = reservationList[keyPosition].guestName
        val rev_id = reservationList[keyPosition].rev_id
        val Remarks = reservationList[keyPosition].Remarks
        val Email = reservationList[keyPosition].Email
        val additional_fees = reservationList[keyPosition].additional_fees
        val subtotal_amt = reservationList[keyPosition].subtotal_amt
        val total_amt = reservationList[keyPosition].total_amt
        val total_item = reservationList[keyPosition].total_item
        val Contact = reservationList[keyPosition].Contact

        myRef = database.getReference("CheckIn/" + pushkey)
        var details = CheckInModel(
                "${guestName}",
                "${pushkey}",
                "${status}",
                "${Remarks}",
                "${checkInDate}",
                "${checkOutDate}",
                "${Email}",
                additional_fees,
                subtotal_amt,
                total_amt,
                total_item,
                "${Contact}",
                reservationList[keyPosition].room_list
        )
        myRef.setValue(details)

        Toast.makeText(applicationContext, "Successfully Checked In", Toast.LENGTH_SHORT).show()

        var ref: DatabaseReference = database.getReference("Reservation/" + key + "/")
        ref?.child("status")?.setValue("CheckIn")
        val intent = Intent(this, CheckInMain::class.java)
        startActivity(intent)
    }

}