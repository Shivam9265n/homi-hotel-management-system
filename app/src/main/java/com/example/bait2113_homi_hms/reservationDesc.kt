package com.example.bait2113_homi_hms


import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.bait2113_homi_hms.objectModel.ReservationList
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.google.firebase.database.*

import io.paperdb.Paper

class reservationDesc : AppCompatActivity() {
    lateinit var key: String
    lateinit var checkInDateString: String
    lateinit var checkOutDateString: String
    lateinit var roomName: TextView
    lateinit var roomCat: TextView
    lateinit var roomImage: ImageView
    lateinit var price: TextView
    lateinit var roomstatus: TextView
    lateinit var checkInDate: TextView
    lateinit var checkOutDate: TextView
    lateinit var addtoCart: Button
    lateinit var editRevBtn: Button
    lateinit var deleteRevBtn: Button
    var startDay: Int = 0
    var endDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_desc)
        Paper.init(applicationContext)
        roomName = findViewById(R.id.roomName)
        roomCat = findViewById(R.id.roomCat)
        roomImage = findViewById(R.id.roomImage)
        price = findViewById(R.id.prod_price)
        roomstatus = findViewById(R.id.roomstatus)
        checkInDate = findViewById(R.id.checkInDate)
        checkOutDate = findViewById(R.id.checkOutDate)

        if (intent.extras != null) {
            key = intent.getStringExtra("key").toString()
            checkInDateString = intent.getStringExtra("category").toString()
            checkOutDateString = intent.getStringExtra("category2").toString()

            val items1: List<String> = checkInDateString.split("-")
            startDay = items1.get(2).toInt()

            val items: List<String> = checkOutDateString.split("-")
            endDay = items.get(2).toInt()

            readItem()
        }
        editRevBtn = findViewById(R.id.editRevBtn)
        deleteRevBtn = findViewById(R.id.deleteRevBtn)
        backTo();
    }


    fun backTo() {
        val back: ImageView = findViewById(R.id.arrow_back_icon)
        back.setOnClickListener {
            onBackPressed()
        }
    }

    fun readItem() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Date/" + checkInDateString + "/" + key + "/")
        val checkBox_bed_AddOn = findViewById<CheckBox>(R.id.checkBox_bed_AddOn)

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var item : RevRoomList = dataSnapshot.getValue(RevRoomList::class.java)!!
                roomName.setText(item.roomName)
                roomCat.setText(item.roomCat)

                val diff = endDay - startDay
                var totalprice : Double = item.roomPrice * diff

                price.setText("RM " + String.format("%.2f", totalprice))
                Glide.with(applicationContext).load(item.roomImage).into(roomImage)
                if(item.roomStatus.equals(("Available"))) {
                    roomstatus.setText(roomstatus.text.toString() + item.roomStatus.toString())
                }else {
                    roomstatus.text = "Unavailable!"
                }

                checkInDate.setText(checkInDate.text.toString() + checkInDateString)
                checkOutDate.setText(checkOutDate.text.toString() + checkOutDateString)

                addtoCart.setOnClickListener() {

                    if(checkBox_bed_AddOn.isChecked()){
                        item.bed_Add_On = 1
                    }else{
                        item.bed_Add_On = 0
                    }

                    var revRoomListItem: RevRoomList = RevRoomList(
                            roomID = item.roomID,
                            roomName = item.roomName,
                            roomCat = item.roomCat,
                            roomImage = item.roomImage,
                            roomPrice = totalprice,
                            roomStatus = item.roomStatus,
                            bed_Add_On = item.bed_Add_On,
                            1,
                            1
                    )
                    ReservationList.addItem(revRoomListItem)
                    while(startDay <= endDay){
                        var ref: DatabaseReference = database.getReference("Date/2021/04/"
                                + (startDay).toString() + "/" + item.roomID + "/")
                        ref?.child("roomStatus")?.setValue("Unavailable")
                        startDay+=1
                    }
                    Toast.makeText(applicationContext, "Successfully Added To Reservation List", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        });
    }
}
