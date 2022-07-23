package com.example.bait2113_homi_hms.payment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.payment.PaymentUtils.Companion.getCurrencyString

class PayByCard : AppCompatActivity() {
    private lateinit var edittextCardNumber: EditText

    lateinit var guestName: String
    private lateinit var phoneNumber: String
    lateinit var email: String
    lateinit var remarks: String
    lateinit var checkInDate: String
    lateinit var checkOutDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_by_card)

        edittextCardNumber = findViewById(R.id.edittext_card_number)

        val textAmountPayable: TextView = findViewById<TextView>(R.id.text_total_amount_payable)
        textAmountPayable.text = getCurrencyString(intent.getStringExtra("grandTotal")?.toDouble())

        // back button
        val buttonBack = findViewById<Button>(R.id.button_back_card)
        buttonBack.setOnClickListener {
            this.onBackPressed()
        }

        if (intent != null) {
            //Log.i("Guest Name", guestName.toString())
            guestName = intent.getStringExtra("guestName").toString()
            phoneNumber = intent.getStringExtra("phoneNumber").toString()
            email = intent.getStringExtra("email").toString()
            remarks = intent.getStringExtra("remarks").toString()
            checkInDate = intent.getStringExtra("checkInDate").toString()
            checkOutDate = intent.getStringExtra("checkOutDate").toString()
        }

    }

    fun confirmPay(view: View?) {
        if (!validateFields())
            Toast.makeText(applicationContext, "Invalid Details!", Toast.LENGTH_LONG).show()
        else {
            val intent = Intent(this, PaymentComplete::class.java)

            val transactionBundle = Bundle()
            transactionBundle.putAll(this.intent.extras)
            transactionBundle.putString("paymentMethod", "card")
            transactionBundle.putString("cardNumber", edittextCardNumber.text.toString())
            intent.putExtra("guestName", guestName)
            intent.putExtra("phoneNumber", phoneNumber)
            intent.putExtra("email", email)
            intent.putExtra("remarks", remarks)
            intent.putExtra("checkInDate", checkInDate)
            intent.putExtra("checkOutDate", checkOutDate)
            intent.putExtras(transactionBundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    private fun validateFields(): Boolean {
        val customisedErrorIcon = ResourcesCompat.getDrawable(resources, R.drawable.error_icon_display, null)

        customisedErrorIcon?.setBounds(
                0, 0,
                customisedErrorIcon.intrinsicWidth,
                customisedErrorIcon.intrinsicHeight
        )

        return if (edittextCardNumber.text.toString().isEmpty()) {
            edittextCardNumber.setError("Required Field!", customisedErrorIcon)
            edittextCardNumber.requestFocus()
            false
        } else
            true
    }

}