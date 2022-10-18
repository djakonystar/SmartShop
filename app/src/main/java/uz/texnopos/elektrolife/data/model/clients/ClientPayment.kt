package uz.texnopos.elektrolife.data.model.clients

import com.google.gson.annotations.SerializedName

data class ClientPayment(
    val cash: Long,
    val card: Long,
    @SerializedName("staff_name")
    val vendor: String,
    @SerializedName("created_at")
    val date: String
)
