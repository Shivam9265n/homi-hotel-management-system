package com.example.bait2113_homi_hms.ChecklistHistory

import java.io.Serializable

class ChecklistHistoryModel() : Serializable {
    var roomName: String? = null
    var roomCat: String? = null
    var dateCreated: String? = null
    var timeCreated: String? = null
    var status: String? = null
    var housekeeper: String? = null
    var roomImage: String? = null

    constructor(roomNo: String?, roomType: String?, date: String?, time: String?, status: String?, housekeeper: String?, roomImage: String?) : this() {
        this.roomName = roomNo
        this.roomCat = roomType
        this.dateCreated = date
        this.timeCreated = time
        this.status = status
        this.housekeeper = housekeeper
        this.roomImage = roomImage
    }
}