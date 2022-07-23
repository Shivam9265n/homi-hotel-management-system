package com.example.bait2113_homi_hms.objectModel

import java.io.Serializable
//
//class Reservation (
//        var guestName: String = "",
//        var rev_id: String = "",
//        var floor: Int = 0,
//        var status: String = "",
//        var Remarks: String = "",
//        var checkInDate: String = "",
//        var checkOutDate: String = "",
//        var Email: String = "",
//        var additional_fees: Double = 0.0,
//        var subtotal_amt: Double = 0.0,
//        var total_amt: Double = 0.0,
//        var total_item: Int = 0,
//        var Contact: String = "",
//        var room_list: MutableList<RevRoomList> = mutableListOf()
//
//) : Serializable

class Reservation {

    var guestName: String = ""
    var rev_id: String = ""
    private var status: String = ""
    //var status: String = ""
    var Remarks: String = ""
    private var checkInDate: String = ""
    private var checkOutDate: String = ""
    var Email: String = ""
    var additional_fees: Double = 0.0
    var subtotal_amt: Double = 0.0
    var total_amt: Double = 0.0
    var total_item: Int = 0
    var Contact: String = ""
    var room_list: MutableList<RevRoomList> = mutableListOf()

    constructor(guestName:String, rev_id:String, status:String, Remarks:String, checkInDate: String, checkOutDate:String, Email:String,
                additional_fees:Double, subtotal_amt:Double, total_amt:Double, total_item: Int, Contact: String, room_list: MutableList<RevRoomList>):this(){
        this.guestName = guestName
        this.rev_id = rev_id
        this.status = status
        this.Remarks = Remarks
        this.checkInDate = checkInDate
        this.checkOutDate = checkOutDate
        this.Email = Email
        this.additional_fees = additional_fees
        this.subtotal_amt = subtotal_amt
        this.total_amt = total_amt
        this.total_item = total_item
        this.Contact = Contact
        this.room_list = room_list
    }

    constructor(){
    }

    fun getStatus(): String {
        return status
    }

    fun setStatus(status: String){
        this.status = status
    }

    fun getCheckInDate(): String {
        return checkInDate
    }

    fun getCheckOutDate(): String {
        return checkOutDate
    }

}