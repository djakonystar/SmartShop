package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("product_id")
    var productId: Int,
    var count: Double,
    @SerializedName("unit_id")
    val unitId: Int,
    var price: Double
)