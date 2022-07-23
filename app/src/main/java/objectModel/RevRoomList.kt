package com.example.bait2113_homi_hms.objectModel

import java.io.Serializable
import java.util.*

class RevRoomList(): Serializable {
    var roomID: String = ""
    var roomName: String = ""
    var roomCat: String = ""
    var roomImage: String = ""
    var roomPrice: Double = 0.00
    var roomStatus: String = ""
    var bed_Add_On: Int = 0
    var roomQty: Int = 0
    var floor: Int = 1

    constructor(roomID:String, roomName:String, roomCat:String, roomImage:String, roomPrice:Double, roomStatus: String, bed_Add_On:Int, roomQty:Int, floor:Int):this(){
        this.roomID = roomID
        this.roomName = roomName
        this.roomCat = roomCat
        this.roomImage = roomImage
        this.roomPrice = roomPrice
        this.roomStatus = roomStatus
        this.bed_Add_On = bed_Add_On
        this.roomQty = roomQty
        this.floor = floor
    }
}
