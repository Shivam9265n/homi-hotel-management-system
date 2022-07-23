package com.example.bait2113_homi_hms

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bait2113_homi_hms.payment.PaymentActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class InventoryAddActivity : AppCompatActivity() {
    lateinit var arrowBack: ImageView
    lateinit var stockName: EditText
    lateinit var stockQty: EditText
    lateinit var stockMinQty: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_stock)

        stockName = findViewById<EditText>(R.id.prodName_text_input)
        stockQty = findViewById<EditText>(R.id.stock_count_text_input)
        stockMinQty = findViewById<EditText>(R.id.limit_stock_count_text_input)

        val addBtn: Button = findViewById(R.id.Add_btn)
        addBtn.setOnClickListener() {
            if (validateInventoryDetails())
                addInventory()
        }
        arrowBack = findViewById<ImageView>(R.id.arrow_back_icon)

        arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun addInventory() {
        if (!stockName.equals("") && !stockQty.equals("") && !stockMinQty.equals("")) {
            val inventoryRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Inventory")
            var pushkey: String? = inventoryRef.push().key
            //val inventoryId: String = inventoryRef.push().key
            val stockList = InventoryModel("${pushkey}", stockName.text.toString(), stockMinQty.text.toString().toInt(), stockQty.text.toString().toInt())
            if (pushkey != null) {
                inventoryRef.child(pushkey).setValue(stockList)
                Toast.makeText(this, R.string.toast_add_stock_msg, Toast.LENGTH_LONG).show() //show successful msg
                //super.onBackPressed()
                val intent = Intent(applicationContext, stockMain2::class.java)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, R.string.toast_inv_stock_msg, Toast.LENGTH_LONG).show() //show error msg
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

        if (stockMinQty.text.toString().isEmpty()) {
            stockMinQty.setError("Required Field!", customisedErrorIcon)
            stockMinQty.requestFocus()
            return false
        }


        return true
    }
}