package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class TransactionTransfer(
    val productId: Int,
    val productName: String,
    val count: Double,
    val unitId: Int,
    val price: Price,
    @SerializedName("max_price")
    val maxPrice: MaxPrice,
    @SerializedName("min_price")
    val minPrice: MinPrice,
    @SerializedName("whole_price")
    val wholePrice: WholePrice
)
