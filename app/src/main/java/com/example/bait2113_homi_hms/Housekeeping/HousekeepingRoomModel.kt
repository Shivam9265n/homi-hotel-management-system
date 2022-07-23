package com.example.bait2113_homi_hms.Housekeeping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.Serializable

class HousekeepingRoomModel() : Serializable {
    var roomName: String? = null
    var roomCat: String? = null
    var floor: Int? = null
    var roomImage: String? = null

    constructor(roomName: String?, roomCat: String?, floor: Int?, roomImage: String?) : this() {
        this.roomName = roomName
        this.roomCat = roomCat
        this.floor = floor
        this.roomImage = roomImage
    }

}