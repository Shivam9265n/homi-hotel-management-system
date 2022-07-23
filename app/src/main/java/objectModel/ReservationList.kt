package com.example.bait2113_homi_hms.objectModel

import android.content.Context
import android.util.Log
import io.paperdb.Paper

class ReservationList {
    companion object {
        fun addItem(revRoomListItem: RevRoomList) {
            val revRoomList = getRevList()

            val targetItem = revRoomList.singleOrNull { it.roomID == revRoomListItem.roomID }
            if (targetItem == null) {

                revRoomList.add(revRoomListItem)
            } else {
                targetItem.roomQty += revRoomListItem.roomQty
            }
            saveRevList(revRoomList)
        }

        fun removeItem(roomID:String, context: Context ){
            val revRoomList = getRevList()
            var track: Int = 0

            while(track < revRoomList.size){
                if(revRoomList[track].roomID.equals(roomID)){
                    Log.i("CHECKOUT", revRoomList[track].roomID)
                    revRoomList[track].roomStatus = "Unselected"
                    revRoomList[track].roomQty = 0
                }
                track+=1
            }
            saveRevList(revRoomList)
        }

        fun resumeItem(roomID:String, context: Context ){
            val revRoomList = getRevList()
            var track: Int = 0

            while(track < revRoomList.size){
                if(revRoomList[track].roomID.equals(roomID)){
                    Log.i("CHECKIN", revRoomList[track].roomID)
                    revRoomList[track].roomStatus = "Reserved"
                    revRoomList[track].roomQty = 1
                }
                track+=1
            }
            saveRevList(revRoomList)
        }

        fun removeMoreThanOneItem(revRoomListItem: List<RevRoomList>, context: Context) {
            val revRoomList = getRevList()
            for (item in revRoomListItem) {
                val targetitem = revRoomList.singleOrNull { item.roomID == it.roomID }
                if (targetitem != null) {
                    revRoomList.remove(targetitem)
                }
            }
            saveRevList(revRoomList)
        }

        fun saveRevList(revRoomList: MutableList<RevRoomList>) {
            Paper.book().write("revList", revRoomList)
        }

        fun getRevList(): MutableList<RevRoomList> {
            return Paper.book().read("revList", mutableListOf())
        }

        fun getRevListSize(): Int {
            var revSize = 0
            getRevList().forEach {
                revSize += it.roomQty;
            }
            return revSize
        }

        fun updateRev(revRoomListItem: RevRoomList, context: Context) {
            val revRoomList = getRevList()

            val targetItem = revRoomList.singleOrNull { it.roomID == revRoomListItem.roomID }
            if (targetItem != null) {
                if (targetItem.roomQty > 0) {
                    targetItem.roomQty = revRoomListItem.roomQty
                }
            }
            saveRevList(revRoomList)
        }

        fun calcSubTotal(): Double {
            val revRoomList = getRevList()
            var subTotal: Double = 0.0
            for (room in revRoomList) {
                if(room.roomStatus.equals("Reserved")){
                    subTotal += room.roomPrice * room.roomQty
                }
            }
            return subTotal
        }

        fun calcTotal(): Double {
            val revRoomList = getRevList()
            var payment: Double = 0.0
            for (room in revRoomList) {
                if(room.roomStatus.equals("Reserved")){
                    payment += room.roomPrice * room.roomQty
                    if(room.bed_Add_On == 1){
                        payment += 100
                    }
                }
            }
            return payment
        }

        fun clear(){
            val revRoomList = getRevList()
            revRoomList.clear()
            saveRevList(revRoomList)
        }
    }
}