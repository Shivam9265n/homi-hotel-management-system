package com.example.bait2113_homi_hms.ChecklistHistory

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.*
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingActivity
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.report.ReportActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class ChecklistHistoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var checklistHistoryData = mutableListOf<ChecklistHistoryModel>()
    private var checklistHistoryDataClean = mutableListOf<ChecklistHistoryModel>()
    private lateinit var checklistHistoryAdapter: ChecklistHistoryAdapter
    private var date: String = ""
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checklist_history_main)

        //checklist history
        displayDate()
        //calendar picker---------------------------------------------------------------------------------
        var dateTextView = findViewById<TextView>(R.id.txt_date_checklist_His)

        val cal = Calendar.getInstance()
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                dateTextView!!.setText(sdf.format(cal.getTime()))
                date = dateTextView.text.toString()
            }
        }

        // when you click on the text view, show DatePickerDialog that is set with OnDateSetListener
        dateTextView.setOnClickListener {
            DatePickerDialog(this,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        readChecklist()

        val filterBtn = findViewById<ImageView>(R.id.filter_date_btn)
        filterBtn.setOnClickListener {
            checklistHistoryData.clear()
            checklistHistoryDataClean.clear()
            checklistHistoryAdapter.onClean()
            readChecklist()
        }

        //menu
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val header_menu: ImageView = findViewById(R.id.nav_menu)
        header_menu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)

        }
        drawerLayout.closeDrawer(GravityCompat.START)

        navView.setNavigationItemSelectedListener(this)

        auth = FirebaseAuth.getInstance()
    }

    private fun displayDate() {
        val dateTextView = findViewById<TextView>(R.id.txt_date_checklist_His)
        val getDate = Calendar.getInstance()
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateTextView!!.setText(sdf.format(getDate.getTime()))
        date = dateTextView.text.toString()
    }

    private fun readChecklist() {
        // Read from the database
        val database = FirebaseDatabase.getInstance()
        val checklistRef = database.getReference("Housekeeping/")
        checklistRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val model = data.getValue(ChecklistHistoryModel::class.java)
                    checklistHistoryData.add(model as ChecklistHistoryModel)
                }
                for (item in checklistHistoryData) {
                    if (item.status.equals("Clean") && item.dateCreated.equals(date)) {
                        checklistHistoryDataClean.add(item)

                    }
                }

                val recyclerView = findViewById<RecyclerView>(R.id.checklistHistoryRecyclerView)
                checklistHistoryAdapter = ChecklistHistoryAdapter(applicationContext, checklistHistoryDataClean)
                recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                recyclerView.adapter = checklistHistoryAdapter

                recyclerView.setHasFixedSize((true))

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

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