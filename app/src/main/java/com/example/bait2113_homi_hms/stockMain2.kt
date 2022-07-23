package com.example.bait2113_homi_hms

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.bait2113_homi_hms.Checklist.ChecklistActivity
import com.example.bait2113_homi_hms.ChecklistHistory.ChecklistHistoryActivity
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingActivity
import com.example.bait2113_homi_hms.report.ReportActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class stockMain2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stock_main2)

        var tab_toolbar = findViewById<Toolbar>(R.id.toolbar)
        var tab_viewpager = findViewById<ViewPager>(R.id.tab_viewpager)
        var tab_tablayout = findViewById<TabLayout>(R.id.tab_tablayout)
        var addStock = findViewById<ImageView>(R.id.addStock)

        // set NoActionBar as theme to this activity using setSupportActionBar
        setSupportActionBar(tab_toolbar)
        setupViewPager(tab_viewpager)

        // setupWithViewPager() to show tabs when activity opened
        tab_tablayout.setupWithViewPager(tab_viewpager)

        auth = FirebaseAuth.getInstance()

        addStock.setOnClickListener() {
            val intent = Intent(this, InventoryAddActivity::class.java)
            startActivity(intent)
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val header_menu : ImageView = findViewById(R.id.nav_menu)
        header_menu.setOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        navView.setNavigationItemSelectedListener(this)
    }

    // add items in arraylist and assign adapter to view pager
    private fun setupViewPager(viewpager: ViewPager) {
        var adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(AllStockFragment(), "All Stock")
        adapter.addFragment(LowStockFragment(), "Low Stock")

        // setting adapter to view pager.
        viewpager.setAdapter(adapter)
    }

    // This "ViewPagerAdapter" class overrides functions which are
    // necessary to get information about which item is selected, title
    class ViewPagerAdapter : FragmentPagerAdapter {

        // objects of arraylist. One is of Fragment type and
        // another one is of String type.*/
        private final var fragmentList1: ArrayList<Fragment> = ArrayList()
        private final var fragmentTitleList1: ArrayList<String> = ArrayList()

        // this is a secondary constructor of ViewPagerAdapter class.
        public constructor(supportFragmentManager: FragmentManager)
                : super(supportFragmentManager)

        // returns which item is selected from arraylist of fragments.
        override fun getItem(position: Int): Fragment {
            return fragmentList1.get(position)
        }

        // returns item selected from arraylist of titles.
        @Nullable
        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList1.get(position)
        }

        // returns number of items present in arraylist.
        override fun getCount(): Int {
            return fragmentList1.size
        }

        // adds the fragment and title in 2 separate arraylist.
        fun addFragment(fragment: Fragment, title: String) {
            fragmentList1.add(fragment)
            fragmentTitleList1.add(title)
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