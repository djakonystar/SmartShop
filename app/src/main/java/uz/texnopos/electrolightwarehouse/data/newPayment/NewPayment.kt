package uz.texnopos.electrolightwarehouse.data.newPayment

import com.google.gson.annotations.SerializedName

data class NewPayment(
    @SerializedName("client_id")
    val clientId: Int,
    val cash: String,
    val card: String,
    @SerializedName("created_at")
    val createdAt: String,
    val comment: String?,
)
