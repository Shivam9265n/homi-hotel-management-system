package com.example.bait2113_homi_hms

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.adapter.ItemAdapter
import com.example.bait2113_homi_hms.objectModel.ReservationList
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddReservation : AppCompatActivity(), ItemAdapter.OnItemClickListener{

    private var  allDataset :  ArrayList<RevRoomList> = arrayListOf()
    private var  allTempDataset :  MutableList<RevRoomList> = mutableListOf()
    private var  myDataset :  MutableList<RevRoomList> = mutableListOf()
    lateinit var searchView: androidx.appcompat.widget.SearchView
    lateinit var toolbar: Toolbar
    lateinit var adapter: ItemAdapter
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private var mStorageRef: StorageReference? = null
    lateinit var pushlist: List<RevRoomList>
    var checkInDate: String = "2021-04-21"
    var checkOutDate: String = "2021-04-23"
    lateinit var search_icon: ImageView
    lateinit var checkInBtn: Button
    lateinit var checkOutBtn: Button

    var startYear: Int = 2021
    var startMonth: Int = 4
    var startDay: Int = 24

    var endYear: Int = 2021
    var endMonth: Int = 4
    var endDay: Int = 26

    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_reservation)
        Paper.init(this)
        // Initialize data.
        if (intent.extras != null) {

            checkInDate = intent.getStringExtra("checkInDate").toString()
            checkOutDate = intent.getStringExtra("checkOutDate").toString()
            val items1: List<String> = checkInDate.split("-")

            startYear = items1.get(0).toInt()
            startMonth = items1.get(1).toInt()
            startDay = items1.get(2).toInt()

            val items: List<String> = checkOutDate.split("-")
            endYear = items.get(0).toInt()
            endMonth = items.get(1).toInt()
            endDay = items.get(2).toInt()
        }

        readDataSearch()
        mStorageRef = FirebaseStorage.getInstance().getReference()

        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP;
        flexManager.flexDirection = FlexDirection.ROW;
        flexManager.alignItems = AlignItems.FLEX_START

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        callRecyclerView(this, recyclerView, flexManager, myDataset as ArrayList<RevRoomList>)

        toolbar = findViewById<Toolbar>(R.id.addRev_toolbar)
        setSupportActionBar(toolbar)

        val floatingbutton: FloatingActionButton = findViewById(R.id.floating_action_button)
        floatingbutton.setOnClickListener() {
            val intent = Intent(this, ReservationStoredList::class.java)
            intent.putExtra("checkInDate", checkInBtn.text.toString())
            intent.putExtra("checkOutDate", checkOutBtn.text.toString())
            startActivity(intent)
        }
        //calendar picker---------------------------------------------------------------------------------
        // get the references from layout file
        checkInBtn = findViewById(R.id.checkInDateBtn)
        checkOutBtn = findViewById(R.id.checkOutDateBtn)

        var cal = Calendar.getInstance()

        // create checkIn an OnDateSetListener
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
                checkInBtn!!.setText(sdf.format(cal.getTime()))

                val items1: List<String> = checkInBtn.text.split("-")
                startYear = items1.get(0).toInt()
                startMonth = items1.get(1).toInt()
                startDay = items1.get(2).toInt()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        checkInBtn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@AddReservation,
                    checkInDateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })

        // create checkOut an OnDateSetListener
        val checkOutDateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                checkOutBtn!!.setText(sdf.format(cal.getTime()))

                val items1: List<String> = checkOutBtn.text.split("-")

                endYear = items1.get(0).toInt()
                endMonth = items1.get(1).toInt()
                endDay = items1.get(2).toInt()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        checkOutBtn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@AddReservation,
                    checkOutDateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })

        search_icon = findViewById(R.id.searchIcon)
        search_icon.setOnClickListener{

            if(endDay < startDay){
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Check out date must be grater than check in date!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Okay", null).show()
            }
            myDataset.clear()
            adapter.onClear()
            allTempDataset.clear()
            allDataset.clear()
            readDataSearch()
            //callRecyclerView(this, recyclerView, flexManager, allDataset)
        }
        //calendar picker--------------------------------------------------------------------------------

        //Up button / back--------------------------------------------------------------------------------
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }
        auth = getInstance()
    }

    fun callRecyclerView(
        context: Context,
        recyclerView: RecyclerView,
        flexManager: FlexboxLayoutManager,
        allDataset: ArrayList<RevRoomList>
    ){
        adapter = ItemAdapter(this, allDataset, this, allDataset)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = flexManager
    }

    //ON EACH ITEM CLICKED
    override fun onItemClick(position: Int) {

        val myIntent = Intent(this, RoomDesc::class.java)
        val key = myDataset[position].roomID    //S0001

        myIntent.putExtra("key", key)
        myIntent.putExtra("checkInDate", checkInBtn.text.toString())
        myIntent.putExtra("checkOutDate", checkOutBtn.text.toString())
        startActivity(myIntent)
    }

    override fun addRevStoredList(position: Int) {

        var subPrice = myDataset[position].roomPrice
        val diff = endDay - startDay
        subPrice = subPrice * diff

        val obj = RevRoomList(
                roomID = myDataset[position].roomID,
                roomName = myDataset[position].roomName,
                roomCat = myDataset[position].roomCat,
                roomImage = myDataset[position].roomImage,
                roomPrice = subPrice,
                roomStatus = "Reserved",
                bed_Add_On = myDataset[position].bed_Add_On,
                1,
                floor = myDataset[position].floor
        )
        ReservationList.addItem(obj)
        Toast.makeText(applicationContext, "Successfully Added To Cart", Toast.LENGTH_SHORT).show()

        val database = FirebaseDatabase.getInstance()

        var startDay1 = startDay
        var endDay1 = endDay

        while(startDay1 <= endDay){
            var ref: DatabaseReference = database.getReference("Date/2021/04/" + (startDay1).toString() + "/" + myDataset[position].roomID + "/")
            ref?.child("roomStatus")?.setValue("Unavailable")
            startDay1+=1
        }
        myDataset.clear()
        adapter.onClear()
        allTempDataset.clear()
        allDataset.clear()
        readDataSearch()
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
                adapter.filter.filter(newText)
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

    fun readDataSearch(){
        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        var coun = 0
        var track: Boolean = false
        var add: Boolean = true
        var tempList = mutableListOf<String>()

        var startDay1 = startDay
        var endDay1 = endDay
        var differenceDay = endDay - startDay + 1

        while (startDay1 <= endDay1) {

            // Write a message to the database
            Log.i("startDay1", startDay.toString())
            var myRef =
                database.getReference("Date/2021/0" + startMonth.toString() + "/" + startDay1.toString() + "/")
            // Read from the database

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    allTempDataset.clear()
                    for (childDataSnapshot: DataSnapshot in dataSnapshot.getChildren()) {
                        childDataSnapshot.getValue(RevRoomList::class.java)?.let {
                            Log.i("count", (coun).toString())

                            if (!track) {
                                Log.i("if track", "if track")
                                if (it.roomStatus.equals("Available")) {
                                    allTempDataset.add(it)
                                    tempList.add(it.roomName)
                                    track = true
                                }
                            }else{
                                Log.i("else", "else")
                                var i: Int = 0
                                while(i < tempList.size){
                                    Log.i("position", i.toString())
                                    if(it.roomName.equals(tempList[i])){
                                        add = false
                                    }
                                    i += 1
                                }
                                if(add){
                                    if (it.roomStatus.equals("Available")) {
                                        allTempDataset.add(it)
                                        tempList.add(it.roomName)
                                    }
                                }
                            }
                            coun += 1
                        }
                    }

                    allDataset.addAll(allTempDataset)
                    myDataset.clear()
                    myDataset.addAll(allDataset)
                    adapter.notifyItemInserted(allDataset.size)

                        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
                        adapter = ItemAdapter(applicationContext, myDataset, this@AddReservation, myDataset)
                        recyclerView.layoutManager = FlexboxLayoutManager(applicationContext)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize((true))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            });
            startDay1 += 1
        }
        if (myDataset.size == 0) {
            //Toast.makeText(applicationContext,"There is no available room!",Toast.LENGTH_SHORT).show()
        }
    }
}