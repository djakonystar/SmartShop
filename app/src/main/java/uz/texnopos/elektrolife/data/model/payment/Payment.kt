package uz.texnopos.elektrolife.data.model.payment

import com.google.gson.annotations.SerializedName
import uz.texnopos.elektrolife.data.model.generic.User

data class Payment(
    @SerializedName("payment_id")
    val id: Int,
    @SerializedName("basket_id")
    val basketId: Int,
    val employee: User,
    val client: User,
    @SerializedName("amount_paid")
    val amount: Amount,
    @SerializedName("paid_time")
    val time: String
)
