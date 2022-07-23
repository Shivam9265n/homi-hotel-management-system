package com.example.bait2113_homi_hms

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.stock_list2.*

class EditInventoryActivity : AppCompatActivity() {
    lateinit var key: String
    lateinit var item: InventoryModel
    lateinit var prodLimit: String
    lateinit var prodQty: String
    lateinit var prodName: String
    lateinit var stockName: EditText
    lateinit var stockQty: EditText
    lateinit var stcokLimit: EditText
    lateinit var arrowBack: ImageView
    lateinit var saveBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stock_details_edit)
        if (intent.extras != null) {
            key = intent.getStringExtra("NUMBER").toString()
            prodName = intent.getStringExtra("STOCK").toString()
            prodQty = intent.getStringExtra("QTY").toString()
            prodLimit = intent.getStringExtra("LIMIT").toString()
        }

        stockName = findViewById<EditText>(R.id.prodName_text_input)
        stockQty = findViewById<EditText>(R.id.stock_count_text_input)
        stcokLimit = findViewById<EditText>(R.id.limit_stock_count_text_input)
        stockName.setText(prodName)
        stockQty.setText(prodQty)
        stcokLimit.setText(prodLimit)

        saveBtn = findViewById<Button>(R.id.stock_save_btn)
        saveBtn.setOnClickListener {
            if (validateInventoryDetails()) {
                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle(R.string.dialogTitle_save)
                //set message for alert dialog
                builder.setMessage(R.string.stockDialogMessage_save)
                builder.setIcon(R.drawable.account_save_icon)

                //performing positive action
                builder.setPositiveButton(getString(R.string.yes))
                { dialogInterface, which ->
                    Toast.makeText(applicationContext, "Successfully save the details", Toast.LENGTH_SHORT).show()
                    updateData()

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
        arrowBack = findViewById<ImageView>(R.id.arrow_back_icon)

        arrowBack.setOnClickListener {
            onBackPressed()

        }

    }

    private fun updateData() {
        val inventoryRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Inventory/")
        item = InventoryModel(key, stockName.text.toString(), stcokLimit.text.toString().toInt(), stockQty.text.toString().toInt())
        if (key != null) {
            inventoryRef.child(key).setValue(item)
            //super.onBackPressed()
            val intent = Intent(applicationContext, stockMain2::class.java)
            startActivity(intent)
        }
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

    private fun validateInventoryDetails(): Boolean {
        val customisedErrorIcon = resources.getDrawable(R.drawable.error_icon_display) //getDrawable(int, Resources.Theme) instead.

        customisedErrorIcon?.setBounds(
                0, 0,
                customisedErrorIcon.intrinsicWidth,
                customisedErrorIcon.intrinsicHeight
        )

        if (stockName.text.toString().isEmpty()) {
            stockName.setError("Required Field!", customisedErrorIcon)
            stockName.requestFocus()
            return false
        }

        if (stockQty.text.toString().isEmpty()) {
            stockQty.setError("Required Field!", customisedErrorIcon)
            stockQty.requestFocus()
            return false
        }

        if (stcokLimit.text.toString().isEmpty()) {
            stcokLimit.setError("Required Field!", customisedErrorIcon)
            stcokLimit.requestFocus()
            return false
        }


        return true
    }
}