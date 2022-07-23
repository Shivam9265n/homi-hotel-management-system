package com.example.bait2113_homi_hms

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bait2113_homi_hms.ChecklistHistory.ChecklistHistoryActivity
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingActivity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.bait2113_homi_hms.report.ReportActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Register")
    private val mAuth = FirebaseAuth.getInstance()
    private val users: FirebaseUser = mAuth.currentUser!!
    private val loggedUser = users.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val textViewToProfile =  findViewById<TextView>(R.id.tvToProfile)
        val imageViewToProfile = findViewById<ImageView>(R.id.ivToProfile)
        val userProfilePic : ImageView = findViewById(R.id.ivToProfile)

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(data: DataSnapshot){
                val image = data.child("$loggedUser/profileImageUrl").value.toString()
                Picasso.get().load(image).into(userProfilePic)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        textViewToProfile.setOnClickListener {
            val intent = Intent(this,userProfile :: class.java)
            startActivity(intent)
        }

        imageViewToProfile.setOnClickListener {
            val intent = Intent(this,userProfile :: class.java)
            startActivity(intent)
        }

        val menuList: ImageView = findViewById(R.id.nav_menu)
        menuList.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        navView.setNavigationItemSelectedListener(this)

        val button1 = findViewById<ImageView>(R.id.button1)
        val button2 = findViewById<ImageView>(R.id.button2)
        button1.setOnClickListener {
            val intent = Intent(this, HousekeepingActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, ChecklistHistoryActivity::class.java)
            startActivity(intent)
        }

        var rBtn = findViewById<ImageView>(R.id.reservationBtn)
        var checkInBtn = findViewById<ImageView>(R.id.checkInBtn)
        var checkOutBtn = findViewById<ImageView>(R.id.checkOutBtn)
        var stockBtn = findViewById<ImageView>(R.id.stockBtn)


        rBtn.setOnClickListener {
            val intent = Intent(this, ReservationMain::class.java)
            startActivity(intent)
        }
        checkInBtn.setOnClickListener {
            val intent = Intent(this, CheckInMain::class.java)
            startActivity(intent)
        }
        checkOutBtn.setOnClickListener {
            val intent = Intent(this, checkOut::class.java)
            startActivity(intent)
        }
        stockBtn.setOnClickListener {
            val intent = Intent(this, stockMain2::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_account -> {
                val intent = Intent(this, userProfile::class.java)
                startActivity(intent)
            }
            R.id.nav_reservation -> {
                val intent = Intent(this, ReservationMain::class.java)
                startActivity(intent)
            }
            R.id.nav_checkIn -> {
                val intent = Intent(this, CheckInMain::class.java)
                startActivity(intent)
            }
            R.id.nav_checkOut -> {
                val intent = Intent(this, checkOut::class.java)
                startActivity(intent)
            }
            R.id.nav_houseKeeping -> {
                val intent = Intent(this, HousekeepingActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_checklist -> {
                val intent = Intent(this, ChecklistHistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_inventory -> {
                val intent = Intent(this, stockMain2::class.java)
                startActivity(intent)

            }
            R.id.nav_reports -> {
                val myintent = Intent(this, ReportActivity::class.java)
                startActivity(myintent)

            }
            R.id.nav_logout -> {

                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle(R.string.dialogTitle)
                //set message for alert dialog
                builder.setMessage(R.string.dialogMessage)
                builder.setIcon(android.R.drawable.ic_lock_lock)

                //performing positive action
                builder.setPositiveButton("Yes")
                { dialogInterface, which ->
                    Toast.makeText(applicationContext, "Logout", Toast.LENGTH_SHORT).show()

                    auth.signOut()
                    val intent = Intent(this, Login::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

                //performing negative action
                builder.setNegativeButton("No")
                { dialogInterface, which ->
                    Toast.makeText(applicationContext, "Clicked No", Toast.LENGTH_SHORT).show()
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()

                // Set other dialog properties
                alertDialog.show()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}