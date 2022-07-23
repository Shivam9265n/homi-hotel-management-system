package com.example.bait2113_homi_hms

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.adapter.revStoredListAdapter
import com.example.bait2113_homi_hms.objectModel.ReservationList
import com.example.bait2113_homi_hms.objectModel.RevRoomList
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class ReservationStoredList : AppCompatActivity(), ButtonClickedListener {

    lateinit var adapter: revStoredListAdapter
    lateinit var totalRevPrice: TextView
    private var revList : MutableList<RevRoomList> = mutableListOf()
    private var deleteList : MutableList<RevRoomList> = mutableListOf()
    private var positionlist: MutableList<Int> = mutableListOf()
    private var revlist: MutableList<RevRoomList> = ReservationList.getRevList()
    lateinit var checkAll: CheckBox
    lateinit var checkout_button : Button
    var subtotal : Double = 0.0
    lateinit var checkInDate: String
    lateinit var checkOutDate: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation_room_list)

        revlist= ReservationList.getRevList()
        val flexManager = FlexboxLayoutManager(this)
        flexManager.flexWrap = FlexWrap.WRAP;
        flexManager.flexDirection = FlexDirection.ROW;
        flexManager.alignItems = AlignItems.FLEX_START

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        adapter = revStoredListAdapter(revlist, this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = flexManager
        checkAll = findViewById(R.id.check_all)

        if (intent.extras != null) {

            checkInDate = intent.getStringExtra("checkInDate").toString()
            checkOutDate = intent.getStringExtra("checkOutDate").toString()
        }

        checkout_button = findViewById(R.id.checkout_button)
        checkAll.setOnClickListener {
            if (checkAll.isChecked) {
                adapter.selectAll();
            } else {
                adapter.unselectAll();
            }
        }

        val dustbin: ImageView = findViewById(R.id.delete_cart)
        dustbin.setOnClickListener {
            if((!(deleteList.isEmpty()) || adapter.isSelectedAll )){

                AlertDialog.Builder(this)
                    .setTitle("Delete Item Confirmation")
                    .setMessage("Are you sure?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes",
                            DialogInterface.OnClickListener { dialog, whichButton ->
                                deleteItem()
                            })
                    .setNegativeButton("No", null).show()
            }
        }

        backTo()
        checkout()
        subtotal = ReservationList.calcTotal()
        totalRevPrice = findViewById(R.id.totalRevPrice)
        totalRevPrice.setText("RM " + String.format("%.2f", subtotal))
    }

    fun deleteItem(){
        if(adapter.isSelectedAll){
            ReservationList.removeMoreThanOneItem(revlist, this)
            checkAll.isChecked = false
            revlist.clear()
            adapter.notifyDataSetChanged()
        }
        else{
            ReservationList.removeMoreThanOneItem(deleteList, this)
        }

        positionlist.sort()
        for(i in positionlist.indices.reversed()){
            revlist.removeAt(positionlist[i])
            adapter.notifyItemChanged(positionlist[i]);
            adapter.notifyItemRangeChanged(positionlist[i], revlist.size)
        }
        positionlist.clear()
        totalRevPrice.setText("RM " + String.format("%.2f", ReservationList.calcTotal()))
    }

    fun backTo() {
        val back: ImageView = findViewById(R.id.arrow_back_icon)
        back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getCheckedItem(item: RevRoomList, check: Boolean, position: Int) {
        if(check){
            deleteList.add(item)
            positionlist.add(position)
            revList.add(item)
        }else{
            if(item in  deleteList){
                deleteList.remove(item)
                positionlist.remove(position)
            }
            for(i in revList){
                if(i.roomID.equals(item.roomID)){
                    revList.remove(item)
                }
            }
        }
    }

    override fun add(item: RevRoomList, position: Int) {
        item.roomQty+= 1
        adapter.notifyItemChanged(position)
        ReservationList.updateRev(item, this)
        totalRevPrice.setText("RM " + String.format("%.2f", ReservationList.calcTotal()))
    }

    fun checkout(){
        checkout_button.setOnClickListener{
            if(ReservationList.getRevListSize() != 0){
                AlertDialog.Builder(this)
                    .setTitle("Check Out Confirmation")
                    .setMessage("Are you sure?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes",
                            DialogInterface.OnClickListener { dialog, whichButton ->
                                adapter.isSelectedAll = false
                                val intent = Intent(this, ReservationGuestDetails::class.java)
                                intent.putExtra("checkInDate", checkInDate)
                                intent.putExtra("checkOutDate", checkOutDate)
                                startActivity(intent)

                                //set the value of
                                var allList: MutableList<RevRoomList> = ReservationList.getRevList()
                                var track = 0
                                var track2 = 0

                                //set unselected item's status as "Unselected"
                                for(i in allList){
                                    for(j in revList){
                                        if(i.roomID.equals(j.roomID)){
                                            allList[track].roomStatus = "Reserved"
                                            ReservationList.resumeItem(i.roomID, this)
                                            break
                                        }else{
                                            ReservationList.removeItem(i.roomID, this)
                                        }
                                    }
                                    track+=1
                                }
                                totalRevPrice.setText("RM " + String.format("%.2f", ReservationList.calcTotal()))
                            })
                    .setNegativeButton("No", null).show()
            }
        }
    }
}