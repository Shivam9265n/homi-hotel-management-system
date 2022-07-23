package objectModel

import com.example.bait2113_homi_hms.payment.PaymentUtils.Companion.dateToString
import java.io.Serializable
import java.util.*

class TransactionModel(): Serializable {
    var id = ""
    var time = dateToString(Calendar.getInstance().time, "yyyy-MM-dd'T'HH:mm:ss")
    var payment_method = ""
    var card_number = ""
    var customer_id = ""
    var tax_amount: Double = 0.00
    var payment_amount: Double = 0.00
    var reservation_id = ""

    constructor(id:String, time:String, paymentMethod:String, cardNumber:String, customerId:String, taxAmount:Double, paymentAmount:Double, reservationId: String):this(){
        this.id = id
        this.time = time
        this.payment_method = paymentMethod
        this.card_number = cardNumber
        this.customer_id = customerId
        this.tax_amount = taxAmount
        this.payment_amount = paymentAmount
        this.reservation_id = reservationId
    }
}