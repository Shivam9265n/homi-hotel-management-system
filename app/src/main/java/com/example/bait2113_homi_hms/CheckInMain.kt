package com.example.bait2113_homi_hms

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.ChecklistHistory.ChecklistHistoryActivity
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingActivity
import com.example.bait2113_homi_hms.adapter.checkInAdapter
import com.example.bait2113_homi_hms.objectModel.Reservation
import com.example.bait2113_homi_hms.objectModel.ReservationViewModel
import com.example.bait2113_homi_hms.report.ReportActivity
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class CheckInMain : AppCompatActivity() , checkInAdapter.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: ReservationViewModel
    lateinit var searchView: androidx.appcompat.widget.SearchView
    lateinit var toolbar: Toolbar
    private val reservationList: MutableList<Reservation> = mutableListOf()
    private val checkInList: MutableList<Reservation> = mutableListOf()
    private var purchaseList = Reservation()
    var key = ""
    private var mStorageRef: StorageReference? = null
    lateinit var checkInDateBtn: Button
    lateinit var searchIcon: ImageView

    private lateinit var checkInAdap: checkInAdapter
    private lateinit var auth: FirebaseAuth
    var checkInDate: String = "2021-04-23"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_in_main)

        reservationList.clear()
        checkInDateBtn = findViewById(R.id.checkInDateBtn)
        searchIcon = findViewById(R.id.searchIcon)
        viewModel = ViewModelProvider(this).get(ReservationViewModel::class.java)

        if (intent.extras != null) {
            key = intent.getStringExtra("key").toString()
            Log.i("Rev Key", key)

        }
        getHistoryData()

        val flexManager = FlexboxLayoutManager(applicationContext)
        flexManager.flexWrap = FlexWrap.WRAP;
        flexManager.flexDirection = FlexDirection.ROW;
        flexManager.alignItems = AlignItems.FLEX_START

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        callRecyclerView(this, recyclerView, flexManager, reservationList)

        toolbar = findViewById<Toolbar>(R.id.Rev_toolbar)
        setSupportActionBar(toolbar)

        searchIcon.setOnClickListener() {
            reservationList.clear()
            getData()
            callRecyclerView(this, recyclerView, flexManager, reservationList)
        }

        var cal = Calendar.getInstance()

        val checkInDateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                    view: DatePicker, year: Int, monthOfYear: Int,
                    dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                checkInDateBtn!!.setText(sdf.format(cal.getTime()))

                checkInDate = checkInDateBtn.text.toString()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        checkInDateBtn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                        this@CheckInMain,
                        checkInDateSetListener,
                        // set DatePickerDialog to point to today's date when it loads up
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })

        mStorageRef = FirebaseStorage.getInstance().getReference()
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val header_menu : ImageView = findViewById(R.id.nav_menu)
        header_menu.setOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        navView.setNavigationItemSelectedListener(this)

        auth = FirebaseAuth.getInstance()
    }

    fun callRecyclerView(
            context: Context,
            recyclerView: RecyclerView,
            flexManager: FlexboxLayoutManager,
            reservationList: MutableList<Reservation>
    ){
        checkInAdap = checkInAdapter(this, reservationList, this, reservationList)

        recyclerView.layoutManager = flexManager
        recyclerView.adapter = checkInAdap
    }

    fun getData(){
        //Write a message to the database
        val database = FirebaseDatabase.getInstance()
        var myRef = database.getReference("Reservation/")
        var item = Reservation()
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot : DataSnapshot) {
                item = dataSnapshot.getValue(Reservation::class.java)!!

                var i: Int = 0
                while(i <= 100){
                    if(item.getStatus().equals("Reserved"))
                        if(item.getCheckInDate().equals(checkInDate)){
                            reservationList.add(item)
                            checkInAdap.notifyItemInserted(reservationList.size)
                        }
                }
            }
            override  fun onCancelled(databaseError : DatabaseError) {

            }
        });
    }

    fun getHistoryData(){

        //Write a message to the database
        val database = FirebaseDatabase.getInstance()
        var myRef = database.getReference("Reservation/" + key + "/")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot : DataSnapshot) {
                for ( childDataSnapshot : DataSnapshot in dataSnapshot.getChildren()) {
                    myRef = database.getReference("Reservation/" + key + "/" + childDataSnapshot.getKey().toString() + "/")
                    childDataSnapshot.getValue(Reservation::class.java)?.let {
                        if(it.getStatus().equals("Reserved"))
                            reservationList.add(it)
                    }
                    checkInAdap.notifyItemInserted(reservationList.size)
                }
            }
            override  fun onCancelled(databaseError : DatabaseError) {

            }
        });

        //get item list
        var item = Reservation()

        var itemRef = database.getReference("CheckIn/" + key + "/")

        itemRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) = if (snapshot.exists()) {
                item = snapshot.getValue(Reservation::class.java)!!
                for (i in item.room_list) {
                    purchaseList.room_list.add(i)
                    //historyAdapter.notifyItemInserted(purchaseList.room_list.size)
                }
            } else {
                Toast.makeText(applicationContext,"Opps, You do not have any reservation yet !", Toast.LENGTH_LONG).show()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.i("cancel","noOrderHistoryFile")
            }

        });
    }

    override fun onItemClick(position: Int) {
        val myIntent = Intent(this, MyReservation::class.java)
        Log.i("Position Main", position.toString())
        Log.i("checkObj", reservationList[position].rev_id)
        myIntent.putExtra("ReservationKey", reservationList[position].rev_id)
        startActivity(myIntent)
    }

    //edit
    override fun checkIn(position: Int) {
        val intent = Intent(this, checkIn::class.java)
        intent.putExtra("keyPosition", position)
        intent.putExtra("key", reservationList[position].rev_id)
        startActivity(intent)
    }

    // filtering / Search Function
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        var item: MenuItem = menu!!.findItem(R.id.action_search)
        searchView = item.actionView as androidx.appcompat.widget.SearchView
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                toolbar.setBackgroundColor(Color.WHITE)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                searchView.setQuery("", false)
                return true
            }
        })
        searchView.maxWidth = Int.MAX_VALUE
        searchName(searchView)
        return true
    }

    private fun searchName(searchView: androidx.appcompat.widget.SearchView) {
        searchView.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                checkInAdap.filter.filter(newText)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
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