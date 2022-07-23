package com.example.bait2113_homi_hms.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.objectModel.ReservationList
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.example.bait2113_homi_hms.payment.PaymentUtils.Companion.getCurrencyString
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {
    private lateinit var revlist: MutableList<RevRoomList>
    private lateinit var revDate: Date
    private lateinit var textReservationDate: TextView
    private lateinit var textCustomerName: TextView
    private lateinit var textContactNo: TextView
    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var textGrandTotal: TextView
    private lateinit var textTaxAmount: TextView
    private val taxPercentage = 6
    private var subTotal = 0.00
    private var tax = 0.00

    lateinit var email: String
    lateinit var remarks: String
    lateinit var checkInDate: String
    lateinit var checkOutDate: String

    /* generate date for below API level 26 (can't use LocalDateTime.now due to this) , override the toString part of Date */
    /* reference: https://stackoverflow.com/questions/47006254/how-to-get-current-local-date-and-time-in-kotlin */
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formattedDate = SimpleDateFormat(format, locale)
        return formattedDate.format(this)
    }

    /* get current date time */
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun calculateTax(amountToTax: Double, taxAmount: Int): Double {
        return amountToTax * taxAmount / 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Paper.init(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Paper.init(applicationContext);
        revlist = ReservationList.getRevList()
        Log.i("RevList: ", revlist.toString())

        revDate = getCurrentDateTime()
        textContactNo = findViewById(R.id.text_contact_no)
        textReservationDate = findViewById(R.id.text_reservation_date)
        textCustomerName = findViewById(R.id.text_customer_name)
        spinnerPaymentMethod = findViewById(R.id.spinner_payment_method)
        val textSubTotal: TextView = findViewById(R.id.text_subtotal)
        textTaxAmount = findViewById(R.id.text_tax_amount)
        textGrandTotal = findViewById(R.id.text_grand_total)
        textCustomerName = findViewById(R.id.text_customer_name)

        // back button
        val buttonBack = findViewById<Button>(R.id.button_payment_cancel)
        buttonBack.setOnClickListener {
            this.onBackPressed()
        }

        // update text under tax amount
        val textTaxLabel = findViewById<TextView>(R.id.text_tax_amount_label)
        textTaxLabel.append(" ($taxPercentage%): ")

        subTotal = ReservationList.calcTotal()
        tax = calculateTax(subTotal, taxPercentage)

        textReservationDate.text = revDate.toString("yyyy-MM-dd'T'HH:mm:ss")
        textSubTotal.text = getCurrencyString(subTotal)
        textTaxAmount.text = getCurrencyString(tax)
        textGrandTotal.text = getCurrencyString(subTotal + tax)

        if (intent != null) {
            //Log.i("Guest Name", guestName.toString())
            textCustomerName.text = intent.getStringExtra("guestName").toString()
            textContactNo.text = intent.getStringExtra("phoneNumber").toString()
            email = intent.getStringExtra("email").toString()
            remarks = intent.getStringExtra("remarks").toString()
            checkInDate = intent.getStringExtra("checkInDate").toString()
            checkOutDate = intent.getStringExtra("checkOutDate").toString()
        }
    }

    fun startPay(view: View?) {
        val paymentMethod = spinnerPaymentMethod.selectedItem.toString().toLowerCase(Locale.ROOT)
        val transactionBundle = Bundle()
        transactionBundle.putAll(intent.extras)
        transactionBundle.putSerializable("time", this.revDate)
        transactionBundle.putString("paymentAmount", (subTotal + tax).toString())
        val intent = if (paymentMethod == "cash") {
            Intent(this, PayByCash::class.java)
        } else {
            Intent(this, PayByCard::class.java)
        }
        intent.putExtras(transactionBundle)
        intent.putExtra("grandTotal", (subTotal + tax).toString())
        intent.putExtra("guestName", textCustomerName.text)
        intent.putExtra("phoneNumber", textContactNo.text)
        intent.putExtra("email", email)
        intent.putExtra("remarks", remarks)
        intent.putExtra("checkInDate", checkInDate)
        intent.putExtra("checkOutDate", checkOutDate)
        intent.putExtra("taxAmount", tax.toString())
        startActivity(intent)
    }


}