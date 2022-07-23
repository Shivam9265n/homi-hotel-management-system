package com.example.bait2113_homi_hms.Housekeeping

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.*
import com.example.bait2113_homi_hms.Checklist.ChecklistActivity
import com.example.bait2113_homi_hms.ChecklistHistory.ChecklistHistoryActivity
import com.example.bait2113_homi_hms.R
import com.example.bait2113_homi_hms.report.ReportActivity
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.all_stock2.view.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer

class HousekeepingActivity : AppCompatActivity(), HousekeepingAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private var housekeepingData = mutableListOf<HousekeepingModel>()
    private var housekeepingDataTemp = mutableListOf<HousekeepingModel>()
    private lateinit var housekeepingAdapter: HousekeepingAdapter
    private var roomData = arrayListOf<HousekeepingRoomModel>()
    private var roomDataString = arrayListOf<String>()
    private lateinit var selectedRoom: String
    private lateinit var selectedStaff: String
    private var housekeepingRoomNum = arrayListOf<String>()
    private var roomNum = arrayListOf<String>()
    private var housekeeperData = arrayListOf<AssignHousekeeperModel>()
    private var housekeeperDataString = arrayListOf<String>()
    private var floorClick: Int = 0
    private var count: Int = 0
    private var selectedItemIndex = 0
    private var selectedItemIndexRoom = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var spinnerFilter: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.housekeeping_main)

        //filter housekeeping
        val items = listOf("All Floor", "First Floor", "Second Floor", "Third Floor")
        spinnerFilter = findViewById<Spinner>(R.id.floor_filter)

        if (spinnerFilter != null) {
            val adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFilter.adapter = adapter

            spinnerFilter.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    readHousekeepingData(spinnerFilter.selectedItem.toString())
                    count++
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


        val floatingbutton: FloatingActionButton = findViewById(R.id.floating_action_button)
        floatingbutton.setOnClickListener() {
            addHousekeeping()
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

    private fun readHousekeepingData(floorSelected: String) {
        if (count >= 1) {   //check whether count >= 1
            housekeepingDataTemp.clear()
            housekeepingData.clear()
            housekeepingRoomNum.clear()
            housekeepingAdapter.onClear()
        }

        when (floorSelected) {
            "All Floor" -> floorClick = 0
            "First Floor" -> floorClick = 1
            "Second Floor" -> floorClick = 2
            "Third Floor" -> floorClick = 3
        }
        // Read from the housekeeping database
        val database = FirebaseDatabase.getInstance()
        val housekeepingRef = database.getReference("Housekeeping/")
        housekeepingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val model = data.getValue(HousekeepingModel::class.java)
                    if (model?.getStatus().equals("Dirty")) {
                        housekeepingDataTemp.add(model as HousekeepingModel)
                    }
                }
                //filter the housekeeping data based on floor
                if (housekeepingDataTemp.size > 0) {
                    if (floorClick == 0) {
                        for (item in housekeepingDataTemp) {
                            housekeepingData.add(item)
                        }
                    } else {
                        for (item in housekeepingDataTemp) {
                            if (item.getFloor() == floorClick) {
                                housekeepingData.add(item)
                            }
                        }
                    }
                    val recyclerView = findViewById<RecyclerView>(R.id.housekeepingRecyclerView)
                    housekeepingAdapter = HousekeepingAdapter(applicationContext, housekeepingData, this@HousekeepingActivity)
                    recyclerView.layoutManager = FlexboxLayoutManager(applicationContext)
                    recyclerView.adapter = housekeepingAdapter

                    recyclerView.setHasFixedSize((true))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        if (housekeeperDataString.size == 0)
            readStaffData()
        if (roomData.size == 0)
            readRoomData()
    }

    private fun readStaffData() {
        //make sure no duplicate data
        if (housekeeperDataString.size != 0) {
            housekeeperData.clear()
            housekeeperDataString.clear()
        }

        val database = FirebaseDatabase.getInstance()
        // Read from the database
        val staffRef = database.getReference("Staff/")
        staffRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val model = data.getValue(AssignHousekeeperModel::class.java)
                    housekeeperData.add(model as AssignHousekeeperModel)
                }
                //convert to arrayList<String>
                for (item in housekeeperData) {
                    housekeeperDataString.add(item.staffLname.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun readRoomData() {
        //read room database
        val database = FirebaseDatabase.getInstance()
        val getDate = Calendar.getInstance()
        val year = getDate.get(Calendar.YEAR)
        var month = getDate.get(Calendar.MONTH)
        val day = getDate.get(Calendar.DAY_OF_MONTH)
        var month1 = "01"
        when (month) {
            0 -> month1 = "01"
            1 -> month1 = "02"
            2 -> month1 = "03"
            3 -> month1 = "04"
            4 -> month1 = "05"
            5 -> month1 = "06"
            6 -> month1 = "07"
            7 -> month1 = "08"
            8 -> month1 = "09"
            9 -> month1 = "10"
            10 -> month1 = "11"
            11 -> month1 = "12"
        }

        // Read from the database
        val roomRef = database.getReference("Date/" + year + "/" + month1 + "/" + day + "/")
        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val model = data.getValue(HousekeepingRoomModel::class.java)
                    roomData.add(model as HousekeepingRoomModel)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addHousekeeping() {
        readRoomData()   //read database
        for (item in roomData) {                //get room number from the roomData
            roomNum.add(item.roomName.toString())
        }
        for (item in housekeepingData) {          //get housekeeping's room number from the housekeepingData
            housekeepingRoomNum.add(item.roomName.toString())
        }
        val roomDataString1 = roomNum.minus(housekeepingRoomNum)   //get the roomData that is not in the housekeepingRoomNum
        for (item in roomDataString1) {
            roomDataString.add(item)                   //store into arraylist<String>
        }

        val roomList = roomDataString.toTypedArray()  //convert to array
        var selectedRoom: String

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_housekeeping))
        builder.setSingleChoiceItems(roomList, selectedItemIndexRoom) { dialog, which ->
            selectedItemIndexRoom = which
            selectedRoom = roomList[which]
        }
        if (roomList.size == 0) {
            builder.setMessage("No Room Available!!!")
        }
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            if (roomList.size != 0) {
                selectedRoom = roomList[selectedItemIndexRoom]
                val housekeepingRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Housekeeping")

                val housekeepingId: String? = housekeepingRef.push().key
                val cal = Calendar.getInstance()
                var myFormat = "yyyy-MM-dd" // mention the format you need
                var sdf = SimpleDateFormat(myFormat, Locale.US)
                val date = sdf.format(cal.getTime())

                myFormat = "HH:mm:ss" // mention the format you need
                sdf = SimpleDateFormat(myFormat, Locale.US)
                val time = sdf.format(cal.getTime())

                for (item in roomData) {
                    if (selectedRoom.equals(item.roomName)) {
                        val newHousekeepingData = HousekeepingModel(housekeepingId, item.roomName, item.roomCat, date, time, "", "Dirty", item.floor, item.roomImage)
                        if (housekeepingId != null) {
                            housekeepingRef.child(housekeepingId).setValue(newHousekeepingData)
                            //Toast.makeText(applicationContext, "The housekeeping has been updated", Toast.LENGTH_SHORT).show()

                        }
                        break
                    }
                }

                selectedItemIndexRoom = 0
                housekeepingAdapter.onClear()
                housekeepingData.clear()
                housekeepingDataTemp.clear()
                roomData.clear()
                roomNum.clear()
                roomDataString.clear()
                readRoomData()
                Toast.makeText(applicationContext, "The housekeeping has been updated", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            roomData.clear()
            roomNum.clear()
            roomDataString.clear()

            readRoomData()
        }
        builder.show()
    }

    override fun onItemClick(position: Int) {

    }

    override fun assignStaff(position: Int) {
        val housekeeperList = housekeeperDataString.toTypedArray()  //convert to array
        var selectedHousekeeper = housekeeperList[selectedItemIndex]
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.assign_housekeeper))
        builder.setSingleChoiceItems(housekeeperList, selectedItemIndex) { dialog, which ->
            selectedItemIndex = which
            selectedHousekeeper = housekeeperList[which]
        }
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            housekeepingData[position].setHousekeeper(selectedHousekeeper)
            val housekeepingRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Housekeeping")

            housekeepingRef.child(housekeepingData[position].getID().toString()).setValue(housekeepingData[position])
            housekeepingData.clear()
            housekeepingAdapter.onClear()
            housekeepingDataTemp.clear()
            selectedItemIndex = 0
            Toast.makeText(applicationContext, "The housekeeping has been updated", Toast.LENGTH_SHORT).show()

        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->

        }
        builder.show()
    }

    override fun doneHousekeping(position: Int) {
        if (housekeepingData[position].getHousekeeper().equals("")) {
            Toast.makeText(this, "The housekeeper haven't assigned", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Done is clicked. Now checklist is displayed.", Toast.LENGTH_SHORT).show()
            val myIntent = Intent(this, ChecklistActivity::class.java)
            val key = housekeepingData[position].getID()
            myIntent.putExtra("key", key)
            startActivity(myIntent)
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