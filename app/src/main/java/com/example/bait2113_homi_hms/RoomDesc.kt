package com.example.bait2113_homi_hms

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.objectModel.ReservationList
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.google.firebase.database.*
import io.paperdb.Paper

class RoomDesc : AppCompatActivity() {

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

    var startYear: Int = 2021
    var startMonth: Int = 3
    var startDay: Int = 21

    var endYear: Int = 2021
    var endMonth: Int = 3
    var endDay: Int = 23

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

            checkInDateString = intent.getStringExtra("checkInDate").toString()
            checkOutDateString = intent.getStringExtra("checkOutDate").toString()
            val items1: List<String> = checkInDateString.split("-")

            startYear = items1.get(0).toInt()
            startMonth = items1.get(1).toInt()
            startDay = items1.get(2).toInt()

            val items: List<String> = checkOutDateString.split("-")
            endYear = items.get(0).toInt()
            endMonth = items.get(1).toInt()
            endDay = items.get(2).toInt()

            readItem()
        }
        addtoCart = findViewById(R.id.add_to_cart)
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
        val ref = database.getReference("Date/2021/0" + startMonth.toString() + "/" + (startDay).toString() + "/" + key + "/")
        val checkBox_bed_AddOn = findViewById<CheckBox>(R.id.checkBox_bed_AddOn)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var item : RevRoomList = dataSnapshot.getValue(RevRoomList::class.java)!!

                roomName.setText(item.roomName)
                roomCat.setText(item.roomCat)
                price.setText(String.format("%.2f", item.roomPrice))
                Glide.with(applicationContext).load(item.roomImage).into(roomImage)

                if(item.roomStatus.equals("Available")){
                    roomstatus.setText(roomstatus.text.toString() + item.roomStatus)
                }else {
                    roomstatus.text = "Unavailable!"
                }

                checkInDate.setText(checkInDateString)
                checkOutDate.setText(checkOutDateString)

                addtoCart.setOnClickListener() {

                    if(checkBox_bed_AddOn.isChecked()){
                        item.bed_Add_On = 1
                    }else{
                        item.bed_Add_On = 0
                    }

                    var subPrice = item.roomPrice
                    val diff = endDay - startDay
                    subPrice = subPrice * diff

                    var revRoomListItem: RevRoomList = RevRoomList(
                            roomID = item.roomID,
                            roomName = item.roomName,
                            roomCat = item.roomCat,
                            roomImage = item.roomImage,
                            roomPrice = subPrice,
                            roomStatus = "Reserved",
                            bed_Add_On = item.bed_Add_On,
                            1,
                            floor = item.floor
                    )
                    ReservationList.addItem(revRoomListItem)
                    Toast.makeText(applicationContext, "Successfully Added To Reservation List", Toast.LENGTH_SHORT).show()

                    while(startDay <= endDay){
                        val ref = database.getReference("Date/2021/0" + startMonth.toString() + "/" + (startDay).toString() + "/" + key + "/")
                        ref?.child("roomStatus")?.setValue("Unavailable")
                        startDay+=1
                    }
                    direcTAddRev()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        });
    }

    fun direcTAddRev(){
        val intent = Intent(this, AddReservation::class.java)
        intent.putExtra("checkInDate", checkInDateString)
        intent.putExtra("checkOutDate", checkInDateString)
        startActivity(intent)
    }
}

