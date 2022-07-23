package com.example.bait2113_homi_hms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Register")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val loginTextView = findViewById<TextView>(R.id.tvRedirectToLogin);
        loginTextView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent);
        }
        val registerButton = findViewById<Button>(R.id.buttonRegister)
        registerButton.setOnClickListener {
            when {
                TextUtils.isEmpty(etUsername.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@Register,
                        "Please enter username.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(etEmailID.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@Register,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(etPassword.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@Register,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(etRetypePassword.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@Register,
                        "Please enter retype-password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val username: String = etUsername.text.toString().trim { it <= ' '}
                    val email: String = etEmailID.text.toString().trim { it <= ' ' }
                    val password: String = etPassword.text.toString().trim { it <= ' ' }
                    val retypePassword: String = etRetypePassword.text.toString().trim { it <= ' ' }
                    val phoneNumber: String = etPhoneNumber.text.toString().trim { it <= ' '}

                    val userRegister: user = user(username, email, password, phoneNumber)

                    if(password != retypePassword){
                        Toast.makeText(
                            this@Register,
                            "The Retype Password not matched with the Password field, please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else{
                        // Create an instance and create a register user with email and password.
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(
                                OnCompleteListener<AuthResult> { task ->

                                    //If the registration is successfully done
                                    if (task.isSuccessful) {

                                        //Firebase registered user
                                        val firebaseUser: FirebaseUser = auth.currentUser!!
                                        ref.child(firebaseUser.uid).setValue(userRegister)

                                        Toast.makeText(
                                            this@Register,
                                            "You are registered successfully.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent = Intent(this@Register, Login::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        //If the registering is not successful then show error message.
                                        Toast.makeText(
                                            this@Register,
                                            task.exception!!.message.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                    }
                }
            }
        }
    }



}