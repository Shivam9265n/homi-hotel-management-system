package com.example.bait2113_homi_hms.Checklist

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingActivity
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingModel
import com.example.bait2113_homi_hms.InventoryModel
import com.example.bait2113_homi_hms.MainActivity
import com.example.bait2113_homi_hms.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*

class ChecklistActivity2 : AppCompatActivity() {
    private var checklistData = mutableListOf<InventoryModel>()
    private var checklistData1 = mutableListOf<ChecklistModel>()
    private lateinit var checklistAdapter: ChecklistAdapter
    lateinit var key: String
    private val deductQty: Int = 3
    var item: HousekeepingModel = HousekeepingModel()
    lateinit var arrowBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.checklist_main2)

        if (intent.extras != null) {
            key = intent.getStringExtra("key").toString()
        }
        readChecklist()
        val submit = findViewById<Button>(R.id.submit_checklist_btn)
        submit.setOnClickListener {

            val count = checklistAdapter.count
            if (count == checklistAdapter.itemCount) {  //if all checkbox is ticked
                showConfirmMsgDialog()
            } else {
                showErrorMsgToast()
            }
        }

        arrowBack = findViewById<ImageView>(R.id.Checklist_arrow_back_icon)

        arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showConfirmMsgDialog() {
        MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.submut_confirm))
                .setMessage(getString(R.string.submut_msg))
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(getString(R.string.no)) { _, _ ->

                }
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    saveChecklist()
                    updateInventory()
                    val intent = Intent(this, HousekeepingActivity::class.java)
                    startActivity(intent)
                }
                .show()
    }

    private fun showErrorMsgToast() {
        Toast.makeText(this, R.string.toast_checklistMsg, Toast.LENGTH_LONG).show()
    }

    private fun saveChecklist() {
        item.setStatus("Clean")
        val checklistRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Housekeeping/")

        if (key != null) {
            checklistRef.child(key).setValue(item)
        }
    }

    private fun updateInventory() {
        for (i in checklistData1.indices) {
            checklistData[i].inventoryId = checklistData1[i].inventoryId
            checklistData[i].prodName = checklistData1[i].prodName
            checklistData[i].minStock = checklistData1[i].minStock
            checklistData[i].qty = checklistData1[i].qty
        }
        for (item in checklistData.indices) {
            checklistData[item].qty = checklistData[item].qty?.minus(deductQty)
        }
        val inventoryRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Inventory")
        for (item in checklistData) {
            inventoryRef.child(item.inventoryId.toString()).setValue(item)
        }
    }

    private fun readChecklist() {
        val roomNum: TextView = findViewById<TextView>(R.id.room_no_Checklist2)
        val roomType: TextView = findViewById<TextView>(R.id.room_Type_Checklist2)
        val housekeeper: TextView = findViewById<TextView>(R.id.txt_Housekeeper_Checklist2)
        val dateCreated: TextView = findViewById<TextView>(R.id.txt_Date_Checklist2)
        val timeCreated: TextView = findViewById<TextView>(R.id.txt_Time_Checklist2)
        val image: ImageView = findViewById(R.id.room_Image_Checklist2)
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
                Glide.with(this@ChecklistActivity2).load(item.roomImage).into(image)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        val checklistRef = database.getReference("Inventory/")
        checklistRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val model = data.getValue(InventoryModel::class.java)
                    checklistData.add(model as InventoryModel)
                }
                if (checklistData.size > 0) {
                    for (i in checklistData.indices) {
                        var checklisTemp = ChecklistModel(checklistData[i].inventoryId, checklistData[i].prodName, checklistData[i].minStock, checklistData[i].qty)
                        checklistData1.add(checklisTemp)
                    }
                    val recyclerView = findViewById<RecyclerView>(R.id.checklistRecyclerView)
                    checklistAdapter = ChecklistAdapter(applicationContext, checklistData1)
                    recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    recyclerView.adapter = checklistAdapter
                    checklistAdapter.getItemCount()
                    recyclerView.setHasFixedSize((true))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

}