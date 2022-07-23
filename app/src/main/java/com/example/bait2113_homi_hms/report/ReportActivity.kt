package com.example.bait2113_homi_hms.report

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.bait2113_homi_hms.*
import com.example.bait2113_homi_hms.ChecklistHistory.ChecklistHistoryActivity
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.jaredrummler.materialspinner.MaterialSpinner


class ReportActivity : AppCompatActivity(), ReportDay.OnDataPass, ReportSearch.OnSearchDataPass, NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var spinner: MaterialSpinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val toolbar = findViewById<Toolbar>(R.id.Rev_toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val headerMenu : ImageView = findViewById(R.id.nav_menu)
        headerMenu.setOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        navView.setNavigationItemSelectedListener(this)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // initialize values for report types (for spinner dropdown fragments)
        val reportTypes = listOf("Search", "Daily", "Monthly", "Yearly")

        // attach to the spinner
        spinner = findViewById(R.id.spinner_report_type)
        spinner.setItems(reportTypes)
        spinner.setOnItemSelectedListener { _, _, _, item ->
            changeFragment(item.toString())
        }
        changeFragment(reportTypes[spinner.selectedIndex])
        auth = FirebaseAuth.getInstance()
    }

    private fun changeFragment(fragmentName: String) {
        val fragment: Fragment = when (fragmentName) {
            "Search" -> { ReportSearch.newInstance() }
            "Daily" -> { ReportDay.newInstance() }
            "Monthly" -> { ReportMonth.newInstance() }
            "Yearly" -> { ReportYear.newInstance() }
            else -> {return}
        }
        supportFragmentManager.popBackStack() // garbage collection, clear back stack on leave, prevent leaks
        supportFragmentManager.beginTransaction()
            .replace(R.id.report_fragment, fragment)
            .commitNow()
    }

    override fun onDataPass(data: String) {
        spinner.visibility = View.GONE
        Log.d("DataPass", "hello $data")
        supportFragmentManager.beginTransaction()
            .replace(R.id.report_fragment, InvoiceFragment.newInstance(data))
            .addToBackStack("dayOrSearch")
            .commit()
    }

    override fun onSearchDataPass(data: String) {
        spinner.visibility = View.GONE
        Log.d("DataPass", "hello $data")
        supportFragmentManager.beginTransaction()
                .replace(R.id.report_fragment, InvoiceFragment.newInstance(data))
                .addToBackStack("dayOrSearch")
                .commit()
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

                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
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
                val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()

                // Set other dialog properties
                alertDialog.show()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        spinner.visibility = View.VISIBLE
    }
}