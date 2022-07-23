package com.example.bait2113_homi_hms

import com.example.bait2113_homi_hms.objectModel.RevRoomList


interface ButtonClickedListener {
    fun add(item: RevRoomList, position : Int)
    //fun minus(item: RevRoomList, position: Int)
    fun getCheckedItem(item:RevRoomList, check : Boolean, position: Int)
}