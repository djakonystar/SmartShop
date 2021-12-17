package uz.texnopos.electrolightwarehouse.data.model.newpayment

import com.google.gson.annotations.SerializedName

data class NewPayment(
    @SerializedName("client_id")
    val clientId: Int,
    val cash: Int?,
    val card: Int?,
    val description: String?,
)
