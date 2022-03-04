package uz.texnopos.elektrolife.data.model.newpayment

import com.google.gson.annotations.SerializedName

data class NewPayment(
    @SerializedName("client_id")
    val clientId: Int,
    val cash: Long,
    val card: Long,
    val description: String,
)
