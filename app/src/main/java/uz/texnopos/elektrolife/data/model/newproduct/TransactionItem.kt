package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class TransactionItem(
    @SerializedName("product_id")
    val productId: Int,
    val count: Double
)
