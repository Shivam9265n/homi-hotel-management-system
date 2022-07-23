package com.example.bait2113_homi_hms

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.example.bait2113_homi_hms.payment.PaymentActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.util.regex.Pattern

class ReservationGuestDetails : AppCompatActivity(), View.OnClickListener {

    lateinit var guestName:TextInputEditText
    lateinit var phoneNumber:TextInputEditText
    lateinit var email:TextInputEditText
    lateinit var remarks:TextInputEditText

    lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference?=null
    var database: FirebaseDatabase? =null
    lateinit var checkInDate: String
    lateinit var checkOutDate: String
    private var revList : ArrayList<RevRoomList?> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation_guest_details)

        guestName =findViewById(R.id.guestName)
        phoneNumber =findViewById(R.id.phoneNumber)
        email =findViewById(R.id.email)
        remarks =findViewById(R.id.remarks)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        if (intent.extras != null) {
            checkInDate = intent.getStringExtra("checkInDate").toString()
            checkOutDate = intent.getStringExtra("checkOutDate").toString()
        }


        //Up button
        val go_back = findViewById<ImageView>(R.id.arrow_back_icon)
        go_back.setOnClickListener {
            onBackPressed()
        }

        val customisedErrorIcon = resources.getDrawable(R.drawable.error_icon_display) //getDrawable(int, Resources.Theme) instead.

        customisedErrorIcon?.setBounds(
                0, 0,
                customisedErrorIcon.intrinsicWidth,
                customisedErrorIcon.intrinsicHeight
        )

        //Email Address Validation

        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (email.text.toString().isEmpty())
                    email.setError("Required Field", customisedErrorIcon)
                else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                    email.setError("Invalid Email Address", customisedErrorIcon)
                }
            }
        })

        val proceedPayment_button = findViewById<Button>(R.id.proceedPayment_button)
        proceedPayment_button.setOnClickListener(this)
    }


    private fun validate_guestDetails(): Boolean{
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

        if(guestName.text.toString().isEmpty()){
            guestName.setError("Required Field!", customisedErrorIcon)
            guestName.requestFocus()
            return false
        }

        if(phoneNumber.text.toString().isEmpty()){
            phoneNumber.setError("Required Field!", customisedErrorIcon)
            guestName.requestFocus()
            return false
        }

        if(email.text.toString().isEmpty()){
            email.setError("Required Field!", customisedErrorIcon)
            email.requestFocus()
            return false
        }else{
            val temp = EMAIL_ADDRESS_PATTERN.matcher(email.text.toString()).matches()

            if(!temp){
                email.setError("Wrong email format!", customisedErrorIcon)
            }
            return temp
        }

        if(remarks.text.toString().isEmpty()){
            remarks.setError("Required Field!", customisedErrorIcon)
            remarks.requestFocus()
            return false
        }
        else
            return true
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.proceedPayment_button -> {
                if (!validate_guestDetails())
                    Toast.makeText(applicationContext, "Incomplete Details!", Toast.LENGTH_LONG).show()
                else {
                    val intent = Intent(applicationContext, PaymentActivity::class.java)
                    intent.putExtra("key", guestName.text.toString())
                    intent.putExtra("guestName", guestName.text.toString())
                    intent.putExtra("phoneNumber", phoneNumber.text.toString())
                    intent.putExtra("email", email.text.toString())
                    intent.putExtra("remarks", remarks.text.toString())
                    intent.putExtra("checkInDate", checkInDate)
                    intent.putExtra("checkOutDate", checkOutDate)
                    startActivity(intent)
                }
            }
        }
    }
}
