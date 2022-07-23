package com.example.bait2113_homi_hms.objectModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class ReservationViewModel (application: Application) : AndroidViewModel(application) {

    init {
        Log.i("DeliveryViewModel", "DeliveryViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("DeliveryViewModel", "DeliveryViewModel destroyed!")
    }
}