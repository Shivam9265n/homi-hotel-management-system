package com.example.bait2113_homi_hms.objectModel

import java.io.Serializable

class CheckInModel (

    var guestName: String = "",
    var rev_id: String = "",
    var status: String = "",
    var Remarks: String = "",
    var checkInDate: String = "",
    var checkOutDate: String = "",
    var Email: String = "",
    var additional_fees: Double = 0.0,
    var subtotal_amt: Double = 0.0,
    var total_amt: Double = 0.0,
    var total_item: Int = 0,
    var Contact: String = "",
    var room_list: MutableList<RevRoomList> = mutableListOf()

) : Serializable {

}