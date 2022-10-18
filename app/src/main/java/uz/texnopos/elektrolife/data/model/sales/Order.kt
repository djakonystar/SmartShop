package uz.texnopos.elektrolife.data.model.sales

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("product_name")
    val productName: String,
    val brand: String,
    val count: Double,
    @SerializedName("unit_id")
    val unitId: Int,
    val price: Double,
    val image: String?
)
