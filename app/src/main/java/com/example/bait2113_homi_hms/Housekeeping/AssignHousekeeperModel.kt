package com.example.bait2113_homi_hms.Housekeeping

import java.io.Serializable

class AssignHousekeeperModel() : Serializable {
    var staffLname: String? = null

    constructor(staffName: String?) : this() {
        this.staffLname = staffName
    }
}