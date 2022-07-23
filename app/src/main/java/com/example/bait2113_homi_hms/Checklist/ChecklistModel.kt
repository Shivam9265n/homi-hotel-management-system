package com.example.bait2113_homi_hms.Checklist

import java.io.Serializable

class ChecklistModel() : Serializable {
    var inventoryId: String = ""
    var prodName: String = ""
    var minStock: Int = 0
    var qty: Int = 0
    var isChecked: Boolean = false

    constructor(inventoryId: String, prodName: String, minStock: Int, qty: Int) : this() {
        this.inventoryId = inventoryId
        this.prodName = prodName
        this.minStock = minStock
        this.qty = qty
    }
}