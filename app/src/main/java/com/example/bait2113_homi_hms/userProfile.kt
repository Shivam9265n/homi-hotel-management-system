package com.example.bait2113_homi_hms

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_profile.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class userProfile : AppCompatActivity() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Register")
    private val auth = FirebaseAuth.getInstance()
    private val users: FirebaseUser = auth.currentUser!!
    private val loggedUser = users.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val username : EditText = findViewById(R.id.etNameShow)
        val userPassword : EditText = findViewById(R.id.etPasswordShow)
        val userPhoneNumber : EditText = findViewById(R.id.etPhoneNumberShow)
        val userEmailAddress : EditText = findViewById(R.id.etEmailAddressShow)
        val userProfilePic : ImageView = findViewById(R.id.ivProfile)

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(data: DataSnapshot){
                username.setText(data.child("$loggedUser/username").value.toString())
                userPassword.setText(data.child("$loggedUser/password").value.toString())
                userPhoneNumber.setText(data.child("$loggedUser/phoneNumber").value.toString())
                userEmailAddress.setText(data.child("$loggedUser/email").value.toString())
                val image = data.child("$loggedUser/profileImageUrl").value.toString()
                Picasso.get().load(image).into(userProfilePic)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        buttonUploadPicture.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        buttonLogOut.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonEdit.setOnClickListener{
            etNameShow.setEnabled(true)
            etNameShow.requestFocus()
            etPasswordShow.setEnabled(true)
            etPasswordShow.requestFocus()
            etPhoneNumberShow.setEnabled(true)
            etPhoneNumberShow.requestFocus()

            buttonEdit.setText("Save")
            buttonEdit.setBackgroundColor(Color.GREEN)

            buttonEdit.setOnClickListener{
                ref.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(data: DataSnapshot){
                        val path = data.child("$loggedUser/profileImageUrl").value.toString()
                        val refStore = FirebaseDatabase.getInstance().getReference("/Register/$loggedUser")
                        val newPassword: String = etPasswordShow.text.toString().trim { it <= ' ' }

                        users.updatePassword(newPassword).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("TAG", "Password updated")
                            }
                        }

                        val registerUser = User(etNameShow.text.toString(),etEmailAddressShow.text.toString(),
                            etPasswordShow.text.toString(),etPhoneNumberShow.text.toString(), path)
                        refStore.setValue(registerUser)
                            .addOnSuccessListener {
                            }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })

                val intent = Intent(this,userProfile :: class.java)
                startActivity(intent)
            }
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            ivProfile.setImageBitmap(bitmap)
        }

        uploadImageToFirebaseStorage()
    }

    private fun uploadImageToFirebaseStorage(){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val ref = FirebaseDatabase.getInstance().getReference("/Register/$loggedUser")

        val registerUser = User(etNameShow.text.toString(),etEmailAddressShow.text.toString(),
            etPasswordShow.text.toString(),etPhoneNumberShow.text.toString(), profileImageUrl)
        ref.setValue(registerUser)
            .addOnSuccessListener {
                Toast.makeText(
                    this@userProfile,
                    "Information Update Successful",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    class User(val username: String, val email: String, val password: String,
               val phoneNumber: String, val profileImageUrl: String)

}