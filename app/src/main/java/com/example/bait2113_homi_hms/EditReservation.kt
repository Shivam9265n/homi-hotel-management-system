package com.example.bait2113_homi_hms

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.adapter.editReservationAdapter
import com.example.bait2113_homi_hms.objectModel.Reservation
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.example.bait2113_homi_hms.payment.PaymentUtils
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import objectModel.TransactionModel
import java.util.*
import java.util.regex.Pattern


class EditReservation : AppCompatActivity() , editReservationAdapter.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    var key1 = ""
    lateinit var key: String
    lateinit var keyPosition1: String
    var keyPosition: Int = 0
    var tempKey: Int = 0
    lateinit var clickable_save: ImageView
    lateinit var go_back: ImageView

    lateinit var guestName_edit: EditText
    lateinit var phoneNo_edit: EditText
    lateinit var email_edit: EditText
    lateinit var remarks_edittext: EditText
    private val checkInDate: String = ""
    private val checkOutDate: String = ""

    private lateinit var mUser: Reservation
    private lateinit var auth: FirebaseAuth

    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    private lateinit var editRevAdapter: editReservationAdapter
    private val reservationList2: MutableList<Reservation> = mutableListOf()
    private val reservationList: MutableList<Reservation> = mutableListOf()
    private var purchaseList : MutableList<RevRoomList> = mutableListOf()
    private var allPurchaseList : MutableList<RevRoomList> = mutableListOf()
    private val checkList: MutableList<Reservation> = mutableListOf()

    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_reservation)
        guestName_edit = findViewById(R.id.guestName_edittext)
        phoneNo_edit = findViewById(R.id.phoneNo_edittext)
        email_edit = findViewById(R.id.email_edittext)
        remarks_edittext = findViewById(R.id.remarks_edittext)

        val layoutManager = LinearLayoutManager(this@EditReservation, LinearLayoutManager.HORIZONTAL, false)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView1)
        recyclerView.setLayoutManager(layoutManager)
        editRevAdapter = editReservationAdapter(this, purchaseList, this)
        recyclerView.setAdapter(editRevAdapter)

        if (intent.extras != null) {

            key = intent.getStringExtra("key").toString()
            keyPosition1 = intent.getStringExtra("keyPosition").toString()
            keyPosition = keyPosition1.toInt()
            tempKey = keyPosition

            getData()
            getData2()
            mStorageRef = FirebaseStorage.getInstance().getReference()
        }

        //Up button
        go_back = findViewById<ImageView>(R.id.arrow_back_icon)
        go_back.setOnClickListener {
            onBackPressed()
        }

        auth = FirebaseAuth.getInstance()

        clickable_save = findViewById<ImageView>(R.id.save_icon)
        clickable_save.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.dialogTitle_save)
            //set message for alert dialog
            builder.setMessage(R.string.dialogMessage_save)
            builder.setIcon(R.drawable.account_save_icon)
            if (!validate_save()) {
                Toast.makeText(applicationContext, "Incomplete Details!", Toast.LENGTH_LONG).show()
            }else{
                //performing positive action
                builder.setPositiveButton("Yes")
                { dialogInterface, which ->
                    saveData()
                    Toast.makeText(applicationContext, "Saving", Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "Successfully save the details", Toast.LENGTH_SHORT).show()
                }
                //performing negative action
                builder.setNegativeButton("No")
                { dialogInterface, which ->
                    Toast.makeText(applicationContext, "Clicked no ", Toast.LENGTH_SHORT).show()
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()

                // Set other dialog properties
                alertDialog.show()
            }
        }
    }

    fun getData(){
        var item = Reservation()
        //Write a message to the database
        val database = FirebaseDatabase.getInstance()
        var myRef: DatabaseReference = database.getReference("Reservation/" + key + "/")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                item = dataSnapshot.getValue(Reservation::class.java)!!
                purchaseList.clear()
                guestName_edit.setText(item.guestName)
                phoneNo_edit.setText(item.Contact)
                email_edit.setText(item.Email)
                remarks_edittext.setText(item.Remarks)

                var track = 0
                //get recycler view data
                for (i in item.room_list) {
                    allPurchaseList.add(i)
                    if (i.roomStatus.equals("Reserved")) {
                        purchaseList.add(i)
                    }
                }
                if (purchaseList.size == 0) {
                    Log.i("enterremove", "enterremove")
                    val database2 = FirebaseDatabase.getInstance()
                    var myRef2: DatabaseReference = database2.getReference("Reservation/" + key + "/")
                    myRef2?.child("status")?.setValue("Cancelled")

                    Toast.makeText(applicationContext, "There is no available room!", Toast.LENGTH_SHORT).show()
                }
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView1)
                editRevAdapter = editReservationAdapter(applicationContext, purchaseList, this@EditReservation)
                recyclerView.layoutManager = LinearLayoutManager(this@EditReservation, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = editRevAdapter
                recyclerView.setHasFixedSize((true))
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        });
    }

    fun getData2(){

        //Write a message to the database
        val database = FirebaseDatabase.getInstance()
        var myRef = database.getReference("Reservation/" + key1 + "/")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (childDataSnapshot: DataSnapshot in dataSnapshot.getChildren()) {
                    myRef = database.getReference("Reservation/" + key1 + "/" + childDataSnapshot.getKey().toString() + "/")
                    childDataSnapshot.getValue(Reservation::class.java)?.let {
                        checkList.add(it)
                        if (it.getStatus().equals("Reserved"))
                            reservationList.add(it)
                    }
                    //editRevAdapter.notifyItemInserted(reservationList.size)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        });
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    private fun validate_save(): Boolean {
        val customisedErrorIcon = resources.getDrawable(R.drawable.error_icon_display) //getDrawable(int, Resources.Theme) instead.

        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        )

        customisedErrorIcon?.setBounds(
                0, 0,
                customisedErrorIcon.intrinsicWidth,
                customisedErrorIcon.intrinsicHeight
        )

        if (guestName_edit.text.toString().isEmpty()) {
            guestName_edit.error = "Required Field!"
            guestName_edit.requestFocus()

            return false
        }

        if (phoneNo_edit.text.toString().isEmpty()) {
            phoneNo_edit.error = "Required Field!"
            phoneNo_edit.requestFocus()

            return false
        }

        if (email_edit.text.toString().isEmpty()) {
            email_edit.error = "Required Field!"
            email_edit.requestFocus()

            return false
        }else{
            val temp = EMAIL_ADDRESS_PATTERN.matcher(email_edit.text.toString()).matches()

            if(!temp){
                email_edit.setError("Wrong email format!", customisedErrorIcon)
            }
            return temp
        }

        if (remarks_edittext.text.toString().isEmpty()) {
            remarks_edittext.error = "Required Field!"
            remarks_edittext.requestFocus()

            return false
        }else
            return true
    }

    private fun saveData() {
            val database = FirebaseDatabase.getInstance()
            var myRef: DatabaseReference = database.getReference("Reservation/" + key + "/")

            myRef?.child("guestName")?.setValue(guestName_edit.text.toString())
            myRef?.child("contact")?.setValue(phoneNo_edit.text.toString())
            myRef?.child("email")?.setValue(email_edit.text.toString())
            myRef?.child("remarks")?.setValue(remarks_edittext.text.toString())
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun editReservation(position: Int) {
//        getData2()
        var number = 0

        Log.i("size", reservationList[keyPosition].room_list.size.toString())
        Log.i("purchaseList", purchaseList[0].roomName)
        while(number < reservationList[keyPosition].room_list.size){
            if(purchaseList[position].roomID.equals(allPurchaseList[number].roomID)){

                val intent = Intent(this, editRoom::class.java)
                intent.putExtra("namePosition", number.toString())
                intent.putExtra("keyPosition", keyPosition.toString())
                intent.putExtra("editKey", key)
                startActivity(intent)
            }
            number += 1
        }

    }
}