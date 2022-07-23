package com.example.bait2113_homi_hms.Housekeeping

class HousekeepingModel {
    private var id: String? = null
    var roomName: String? = null
    private var roomCat: String? = null
    private var dateCreated: String? = null
    private var timeCreated: String? = null
    private var housekeeper: String? = null
    private var status: String? = null
    private var floor: Int? = 0
    var roomImage: String? = null

    constructor(id: String?, roomName: String?, roomCat: String?, dateCreated: String?, timeCreated: String?, housekeeper: String?, status: String?, floor: Int?, roomImage: String?) {
        this.id = id
        this.roomName = roomName
        this.roomCat = roomCat
        this.dateCreated = dateCreated
        this.timeCreated = timeCreated
        this.housekeeper = housekeeper
        this.status = status
        this.floor = floor
        this.roomImage = roomImage
    }

    constructor() {

    }

    fun setID(id: String?) {
        this.id = id
    }

    fun setRoomNo(roomName: String?) {
        this.roomName = roomName
    }

    fun setRoomCat(roomCat: String?) {
        this.roomCat = roomCat
    }

    fun setDateCreated(dateCreated: String?) {
        this.dateCreated = dateCreated
    }

    fun setTimeCreated(timeCreated: String?) {
        this.timeCreated = timeCreated
    }

    fun setHousekeeper(housekeeper: String?) {
        this.housekeeper = housekeeper
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun setFloor(floor: Int?) {
        this.floor = floor
    }

    fun getID(): String? {
        return id
    }

    fun getRoomCat(): String? {
        return roomCat
    }

    fun getDateCreated(): String? {
        return dateCreated
    }

    fun getTimeCreated(): String? {
        return timeCreated
    }

    fun getHousekeeper(): String? {
        return housekeeper
    }

    fun getStatus(): String? {
        return status
    }

    fun getFloor(): Int? {
        return floor
    }
}