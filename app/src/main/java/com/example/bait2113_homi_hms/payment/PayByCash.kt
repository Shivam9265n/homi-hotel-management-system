package com.example.bait2113_homi_hms.payment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.payment.PaymentUtils.Companion.getCurrencyString

class PayByCash : AppCompatActivity() {

    lateinit var guestName: String
    private lateinit var phoneNumber: String
    lateinit var email: String
    lateinit var remarks: String
    lateinit var checkInDate: String
    lateinit var checkOutDate: String
    private lateinit var grandTotal: String
    private lateinit var editTextAmountReceived: EditText
    lateinit var textTotalAmountPayable: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_by_cash)
        textTotalAmountPayable = findViewById(R.id.text_total_amount_payable)
        editTextAmountReceived = findViewById(R.id.edit_text_amount_received)
        val textChangeAmount = findViewById<TextView>(R.id.text_change_amount)

        // back button
        val buttonBack = findViewById<Button>(R.id.button_back_card)
        buttonBack.setOnClickListener {
            this.onBackPressed()
        }

        if (intent != null) {
            guestName = intent.getStringExtra("guestName").toString()
            phoneNumber = intent.getStringExtra("phoneNumber").toString()
            email = intent.getStringExtra("email").toString()
            remarks = intent.getStringExtra("remarks").toString()
            checkInDate = intent.getStringExtra("checkInDate").toString()
            checkOutDate = intent.getStringExtra("checkOutDate").toString()
            grandTotal = intent.getStringExtra("grandTotal").toString()
            textTotalAmountPayable.text = getCurrencyString(grandTotal.toDoubleOrNull())
        }

        editTextAmountReceived.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val receivedAmount = s.toString().toDoubleOrNull()
                if (receivedAmount != null) {
                    textChangeAmount.text = getCurrencyString(receivedAmount - grandTotal.toDouble())
                } else {
                    textChangeAmount.text = "-"
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun confirmPay(view: View?) {
        if (!validateFields())
            Toast.makeText(applicationContext, "Invalid Details!", Toast.LENGTH_LONG).show()
        else {
            val intent = Intent(this, PaymentComplete::class.java)

            val transactionBundle = Bundle()
            transactionBundle.putAll(this.intent.extras)
            transactionBundle.putString("paymentMethod", "cash")

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



        return when {
            editTextAmountReceived.text.toString().isEmpty() -> {
                editTextAmountReceived.setError("Required Field!", customisedErrorIcon)
                editTextAmountReceived.requestFocus()
                false
            }
            editTextAmountReceived.text.toString() == "." -> {
                editTextAmountReceived.setError("Cannot enter dot alone!", customisedErrorIcon)
                editTextAmountReceived.requestFocus()
                false
            }
            editTextAmountReceived.text.toString().toDouble() < grandTotal.toDouble() -> {
                editTextAmountReceived.setError("Insufficient Amount!", customisedErrorIcon)
                editTextAmountReceived.requestFocus()
                false
            }
            else -> true
        }


    }
}