package uz.texnopos.elektrolife.data.model.sales

import com.google.gson.annotations.SerializedName

data class Basket(
    val id: Int,
    val card: Double,
    val cash: Double,
    val debt: Double,
    val price: Double,
    @SerializedName("created_at")
    val createdAt: String
)
