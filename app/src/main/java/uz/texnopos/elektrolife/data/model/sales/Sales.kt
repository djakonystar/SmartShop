package uz.texnopos.elektrolife.data.model.sales

import com.google.gson.annotations.SerializedName

data class Sales(
    @SerializedName("client_id")
    val clientId: Int,
    @SerializedName("client_name")
    val clientName: String,
    val phone: String,
    val basket: Basket,
    val orders: List<Product>,
    @SerializedName("vendor_name")
    val vendorName: String
)
