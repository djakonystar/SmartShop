package uz.texnopos.elektrolife.data.model.payment

import com.google.gson.annotations.SerializedName

data class AddPayment(
    @SerializedName("basket_id")
    val basketId: Int,
    val cash: Double,
    val card: Double
)
