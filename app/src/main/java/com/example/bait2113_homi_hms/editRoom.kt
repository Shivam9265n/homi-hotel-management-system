package com.example.bait2113_homi_hms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.objectModel.Reservation
import com.example.bait2113_homi_hms.payment.PaymentUtils
import com.google.firebase.database.*
import io.paperdb.Paper
import objectModel.TransactionModel
import java.util.*

class editRoom  : AppCompatActivity(){
    lateinit var key: String
    var namePosition: Int = 0
    lateinit var namePosition1: String
    var keyPosition: Int = 0
    lateinit var keyPosition1: String
    lateinit var roomName: TextView
    lateinit var roomCat: TextView
    lateinit var roomImage: ImageView
    lateinit var price: TextView
    lateinit var roomstatus: TextView
    lateinit var save_btn: Button
    lateinit var additional_fees: TextView
    lateinit var checkBox_bed_AddOn: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_room_desc)

        Paper.init(applicationContext)
        roomName = findViewById(R.id.roomName)
        roomCat = findViewById(R.id.roomCat)
        roomImage = findViewById(R.id.roomImage)
        additional_fees = findViewById(R.id.additional_fees)
        price = findViewById(R.id.room_price)
        roomstatus = findViewById(R.id.roomstatus)
        checkBox_bed_AddOn = findViewById(R.id.checkBox_bed_AddOn)


        if (intent.extras != null) {
            key = intent.getStringExtra("editKey").toString()

            keyPosition1 = intent.getStringExtra("keyPosition").toString()
            keyPosition = keyPosition1.toInt()

            namePosition1 = intent.getStringExtra("namePosition").toString()
            namePosition = namePosition1.toInt()

            readItem()
        }

        save_btn = findViewById(R.id.save_btn)
        backTo();
    }

    fun backTo() {
        val back: ImageView = findViewById(R.id.arrow_back_icon)
        back.setOnClickListener {
            onBackPressed()
        }
    }

    fun readItem() {
        var item = Reservation()
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Reservation/" + key + "/")
        val checkBox_bed_AddOn = findViewById<CheckBox>(R.id.checkBox_bed_AddOn)
        var payment_Amount : Double = 0.0
        var check : Int = 0

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                item  = dataSnapshot.getValue(Reservation::class.java)!!

                roomName.setText(item.room_list[namePosition].roomName)
                roomCat.setText(item.room_list[namePosition].roomCat)
                roomstatus.setText(item.room_list[namePosition].roomStatus)
                price.setText(item.room_list[namePosition].roomPrice.toString())
                Glide.with(applicationContext).load(item.room_list[namePosition].roomImage).into(roomImage)
                if(item.room_list[namePosition].bed_Add_On == 1){
                    checkBox_bed_AddOn.setChecked(true);
                    additional_fees.setText("RM 100.00")
                }else{
                    checkBox_bed_AddOn.setChecked(false);
                    additional_fees.setText("RM 0.00")
                }

                save_btn.setOnClickListener() {
                    if(checkBox_bed_AddOn.isChecked()){
                        if(item.room_list[namePosition].bed_Add_On == 0){
                            item.room_list[namePosition].bed_Add_On = 1
                            item.additional_fees += 100.00
                            item.total_amt += 100.00
                            additional_fees.setText("100.00")
                            payment_Amount = 100.00
                            check = 1

                        }
                    }else{
                        if(item.room_list[namePosition].bed_Add_On == 1){
                            item.room_list[namePosition].bed_Add_On = 0
                            item.additional_fees -= 100.00
                            item.total_amt -= 100.00
                            payment_Amount = -100.00
                            check = 1
                        }
                    }

                    //val database2 = FirebaseDatabase.getInstance()
                    var myRef2: DatabaseReference = database.getReference("Reservation/" + key + "/")
                    myRef2?.child("additional_fees")?.setValue(item.additional_fees)
                    myRef2?.child("total_amt")?.setValue(item.total_amt)

                    //val database3 = FirebaseDatabase.getInstance()
                    var myRef3: DatabaseReference = database.getReference("Reservation/" + key + "/room_list/" + namePosition + "/")
                    myRef3?.child("bed_Add_On")?.setValue(item.room_list[namePosition].bed_Add_On)
                    val guestName = item.guestName

                    //update transaction table with negative value
                    if(check == 1){
                        val transactionReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Transaction")
                        val id: String? = transactionReference.push().key // TODO: change later
                        val transaction = TransactionModel(
                                "${id}",
                                PaymentUtils.dateToString(Calendar.getInstance().time, "yyyy-MM-dd'T'HH:mm:ss"),
                                "Refund",
                                "",
                                "${guestName}",
                                0.00,
                                payment_Amount,
                                "${key}"
                        )
                        if (id != null) {
                            transactionReference.child(id).setValue(transaction)
                        }
                        if(payment_Amount < 0.0){
                            android.app.AlertDialog.Builder(this@editRoom)
                                    .setTitle("Your refund is being processed")
                                    .setMessage("Most refunds will be posted to the account within 5 to 10 business days.")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton("Okay", null).show()
                        }else{
                            Toast.makeText(applicationContext, "Update successful!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val intent = Intent(this@editRoom, EditReservation::class.java)
                    intent.putExtra("keyPosition", keyPosition.toString())
                    intent.putExtra("key", key)
                    startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        });
    }
}