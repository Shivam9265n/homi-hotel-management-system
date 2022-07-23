package com.example.bait2113_homi_hms

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.ChecklistHistory.ChecklistHistoryActivity
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingActivity
import com.example.bait2113_homi_hms.Housekeeping.HousekeepingModel
import com.example.bait2113_homi_hms.adapter.checkOutAdapter
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

class checkOut : AppCompatActivity() , checkOutAdapter.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: ReservationViewModel
    lateinit var searchView: androidx.appcompat.widget.SearchView
    lateinit var toolbar: Toolbar
    private val checkInList: MutableList<Reservation> = mutableListOf()
    var key = ""
    private var mStorageRef: StorageReference? = null
    private lateinit var checkOutAdap: checkOutAdapter
    private lateinit var auth: FirebaseAuth

    lateinit var checkInDateBtn: Button
    lateinit var searchIcon: ImageView
    var checkInDate: String = "2021-04-23"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_out_main)

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
        callRecyclerView(this, recyclerView, flexManager, checkInList)

        toolbar = findViewById<Toolbar>(R.id.Rev_toolbar)
        setSupportActionBar(toolbar)

        searchIcon.setOnClickListener() {
            checkInList.clear()
            Log.i("checkInDatesss", checkInDate)
            getData()
            callRecyclerView(this, recyclerView, flexManager, checkInList)
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
                        this@checkOut,
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
        checkOutAdap = checkOutAdapter(this, reservationList, this, reservationList)

        recyclerView.layoutManager = flexManager
        recyclerView.adapter = checkOutAdap
    }

    fun getData(){
        //Write a message to the database
        val database = FirebaseDatabase.getInstance()
        var myRef = database.getReference("CheckIn/" + key + "/")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot : DataSnapshot) {

                for ( childDataSnapshot : DataSnapshot in dataSnapshot.getChildren()) {
                    myRef = database.getReference("Reserved/" + key + "/" + childDataSnapshot.getKey().toString() + "/")
                    childDataSnapshot.getValue(Reservation::class.java)?.let {
                        if(it.getStatus().equals("CheckIn"))
                            if(it.getCheckInDate().equals(checkInDate))
                                checkInList.add(it)
                    }
                    checkOutAdap.notifyItemInserted(checkInList.size)
                }
            }
            override  fun onCancelled(databaseError : DatabaseError) {
            }
        });
        if(checkInList.size == 0){
            Toast.makeText(applicationContext,"There is no records!",Toast.LENGTH_SHORT).show()
        }
    }

    fun getHistoryData(){

        val database = FirebaseDatabase.getInstance()
        var myRef = database.getReference("CheckIn/" + key + "/")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot : DataSnapshot) {

                for ( childDataSnapshot : DataSnapshot in dataSnapshot.getChildren()) {
                    myRef = database.getReference("Reserved/" + key + "/" + childDataSnapshot.getKey().toString() + "/")
                    childDataSnapshot.getValue(Reservation::class.java)?.let {
                        if(it.getStatus().equals("CheckIn"))
                            checkInList.add(it)
                    }
                    checkOutAdap.notifyItemInserted(checkInList.size)
                }
            }
            override  fun onCancelled(databaseError : DatabaseError) {
            }
        });
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun checkOut(position: Int) {
        android.app.AlertDialog.Builder(this)
                .setTitle("Check Out Confirmation")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes",
                        DialogInterface.OnClickListener { dialog, whichButton ->
                            checkOutCon(position)
                        })
                .setNegativeButton("No", null).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkOutCon(position: Int){

        val position = position
        val database = FirebaseDatabase.getInstance()
        var myRef: DatabaseReference = database.getReference("CheckIn/" + checkInList[position].rev_id + "/")
        myRef?.child("status")?.setValue("CheckOut")

        addHouseKeeping(position)

        checkOutAdap.deleteRecyclerView(position)
        Toast.makeText(applicationContext, "Successfully Checked Out", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addHouseKeeping(position: Int){
        //write checkout details into database
        val database = FirebaseDatabase.getInstance()
        var myRef: DatabaseReference = database.getReference("Housekeeping/")

        var count : Int = checkInList[position].room_list.size
        Log.i("OriSize", count.toString())
        count -= 1
        Log.i("Minus1Size", count.toString())
        var i : Int = 0

        while(count>=0){
            Log.i("FirstSize", count.toString())
            var pushkey: String?= myRef.push().key
            val roomName = checkInList[position].room_list[count].roomName
            val roomCat = checkInList[position].room_list[count].roomCat
            val floor = checkInList[position].room_list[count].floor
            val image = checkInList[position].room_list[count].roomImage

            val cal = Calendar.getInstance()
            var myFormat = "yyyy-MM-dd" // mention the format you need
            var sdf = SimpleDateFormat(myFormat, Locale.US)
            val date = sdf.format(cal.getTime())

            myFormat = "HH:mm:ss" // mention the format you need
            sdf = SimpleDateFormat(myFormat, Locale.US)
            val time = sdf.format(cal.getTime())

            val housekeeper = ""
            val status = "Dirty"

            //write the details of checkout into database
            myRef = database.getReference("Housekeeping/" + pushkey)

            var details = HousekeepingModel(
                "${pushkey}",
                "${roomName}",
                "${roomCat}",
                "${date}",
                "${time}",
                "${housekeeper}",
                "${status}",
                    floor,
                "${image}"
            )
            myRef.setValue(details)
            count -= 1
        }
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
                checkOutAdap.filter.filter(newText)
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
