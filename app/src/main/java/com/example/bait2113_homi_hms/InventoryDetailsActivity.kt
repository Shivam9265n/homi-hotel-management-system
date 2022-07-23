package com.example.bait2113_homi_hms

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bait2113_homi_hms.Checklist.ChecklistActivity
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingModel
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingRoomModel
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class InventoryDetailsActivity : AppCompatActivity() {
    lateinit var key: String
    lateinit var prodLimit: TextView
    lateinit var prodQty: TextView
    lateinit var prodName: TextView
    lateinit var arrowBack: ImageView
    var item: InventoryModel = InventoryModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stock_details)
        if (intent.extras != null) {
            key = intent.getStringExtra("NUMBER").toString()
            getData()
        }
        val editbutton: Button = findViewById(R.id.edit_btn)
        editbutton.setOnClickListener() {
            val myIntent = Intent(this, EditInventoryActivity::class.java)
            myIntent.putExtra("NUMBER", key)
            myIntent.putExtra("STOCK", prodName.text.toString())
            myIntent.putExtra("QTY", prodQty.text.toString())
            myIntent.putExtra("LIMIT", prodLimit.text.toString())
            startActivity(myIntent)
        }

        val deleteIcon: ImageView = findViewById((R.id.delete_icon))
        deleteIcon.setOnClickListener() {
            android.app.AlertDialog.Builder(this)
                    .setTitle("Delete Item Confirmation")
                    .setMessage("Are you sure?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes",
                            DialogInterface.OnClickListener { dialog, whichButton ->
                                val inventoryRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Inventory")

                                if (key != null) {
                                    inventoryRef.child(key).setValue(null)  //set the value to null in order to delete item
                                    Toast.makeText(this, R.string.toast_delete_stock_msg, Toast.LENGTH_SHORT).show() //show successful msg
                                    //super.onBackPressed()
                                    val intent = Intent(applicationContext, stockMain2::class.java)
                                    startActivity(intent)
                                }
                            })
                    .setNegativeButton("No", null).show()


        }

        arrowBack = findViewById<ImageView>(R.id.arrow_back_icon)

        arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getData() {
        prodName = findViewById<EditText>(R.id.prodName_text_input)
        prodQty = findViewById<EditText>(R.id.stock_count_text_input)
        prodLimit = findViewById<EditText>(R.id.limit_stock_count_text_input)

        val database = FirebaseDatabase.getInstance()
        val roomRef = database.getReference("Inventory/" + key + "/")
        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    item = dataSnapshot.getValue(InventoryModel::class.java)!!
                    prodName.setText(item.prodName.toString())
                    prodQty.setText(item.qty.toString())
                    prodLimit.setText(item.minStock.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}