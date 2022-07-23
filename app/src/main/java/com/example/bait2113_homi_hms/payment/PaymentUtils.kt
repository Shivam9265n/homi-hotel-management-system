package com.example.bait2113_homi_hms.payment

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PaymentUtils {
    companion object {
        // convert date to string
        fun dateToString(date: Date, format: String, locale: Locale = Locale.getDefault()): String {
            val formattedDate = SimpleDateFormat(format, locale)
            return formattedDate.format(date)
        }

        fun getCurrencyString(figure: Double?): String {
            if (figure == null) {
                return "N/A"
            }
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.currency = Currency.getInstance("MYR")
            format.minimumFractionDigits = 2
            return format.format(figure)
        }


    }
}