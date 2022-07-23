package com.example.bait2113_homi_hms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val buttonNext = findViewById<ImageView>(R.id.ivNext)
        buttonNext.setOnClickListener {
            val email: String = etEmail.text.toString().trim { it <= ' '}
            if (email.isEmpty()){
                Toast.makeText(
                    this@ForgotPassword,
                    "Please enter email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            Toast.makeText(
                                this@ForgotPassword,
                                "Email sent successfully to reset your password",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }
                        else{
                            Toast.makeText(
                                this@ForgotPassword,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}