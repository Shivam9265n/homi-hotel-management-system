package com.example.bait2113_homi_hms.Checklist

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingModel
import com.example.bait2113_homi_hms.R
import com.google.firebase.database.*

class ChecklistActivity : AppCompatActivity() {
    lateinit var key: String
    lateinit var arrowBack: ImageView
    var item: HousekeepingModel = HousekeepingModel()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.checklist_main)

        if (intent.extras != null) {
            key = intent.getStringExtra("key").toString()
            readChecklist()
        }

        val continueBtn = findViewById<Button>(R.id.continue_checklist_btn)
        continueBtn.setOnClickListener {
            val cbBed: CheckBox = findViewById<CheckBox>(R.id.checkbox_bed)
            val cbBathroom: CheckBox = findViewById<CheckBox>(R.id.checkbox_bathroom)
            val cbDustbin: CheckBox = findViewById<CheckBox>(R.id.checkbox_dustbin)
            val cbVacuum: CheckBox = findViewById<CheckBox>(R.id.checkbox_vacuum)
            val cbAppliance: CheckBox = findViewById<CheckBox>(R.id.checkbox_appliance)
            val cbTowel: CheckBox = findViewById<CheckBox>(R.id.checkbox_towel)

            if (cbBed.isChecked && cbBathroom.isChecked && cbDustbin.isChecked && cbVacuum.isChecked && cbAppliance.isChecked && cbTowel.isChecked) {
                val myIntent = Intent(this, ChecklistActivity2::class.java)
                myIntent.putExtra("key", key)
                startActivity(myIntent)
            } else
                Toast.makeText(applicationContext, "Please complete this checklist!!!", Toast.LENGTH_SHORT).show()

        }
        arrowBack = findViewById<ImageView>(R.id.Checklist_arrow_back_icon)

        arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun readChecklist() {
        val roomNum: TextView = findViewById<TextView>(R.id.room_no_Checklist)
        val roomType: TextView = findViewById<TextView>(R.id.room_Type_Checklist)
        val housekeeper: TextView = findViewById<TextView>(R.id.txt_Housekeeper_Checklist)
        val dateCreated: TextView = findViewById<TextView>(R.id.txt_Date_Checklist)
        val timeCreated: TextView = findViewById<TextView>(R.id.txt_Time_Checklist)
        val image: ImageView = findViewById(R.id.room_Image_Checklist)
        // Read from the database
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Housekeeping/" + key + "/")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                item = dataSnapshot.getValue(HousekeepingModel::class.java)!!
                roomNum.setText(item.roomName)
                roomType.setText(item.getRoomCat())
                housekeeper.setText(item.getHousekeeper())
                dateCreated.setText(item.getDateCreated())
                timeCreated.setText(item.getTimeCreated())
                Glide.with(this@ChecklistActivity).load(item.roomImage).into(image)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.dialogTitle_discard)
        //set message for alert dialog
        builder.setMessage(R.string.discard_msg)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton(getString(R.string.yes))
        { dialogInterface, which ->
            super.onBackPressed()
        }

        //performing negative action
        builder.setNegativeButton(getString(R.string.no))
        { dialogInterface, which ->

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()

        // Set other dialog properties
        alertDialog.show()
    }

}