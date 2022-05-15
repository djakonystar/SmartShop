package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class TransactionItem(
    @SerializedName("product_id")
    val productId: Int,
    val count: Double,
    @SerializedName("unit_id")
    val unitId: Int,
    val price: Price
)
