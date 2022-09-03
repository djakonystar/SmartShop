package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class TransactionItem(
    @SerializedName("product_id")
    val productId: Int,
    val count: Double,
    @SerializedName("unit_id")
    val unitId: Int,
    val price: Price,
    @SerializedName("max_price")
    val maxPrice: MaxPrice,
    @SerializedName("min_price")
    val minPrice: MinPrice,
    @SerializedName("whole_price")
    val wholePrice: WholePrice
)
