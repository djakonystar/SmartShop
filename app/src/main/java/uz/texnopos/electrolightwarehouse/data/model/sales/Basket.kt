package uz.texnopos.electrolightwarehouse.data.model.sales

import com.google.gson.annotations.SerializedName

data class Basket(
    val id: Int,
    val card: Double,
    val cash: Double,
    val debt: Double,
    val price: Double,
    val term: String,
    val description: String,
    @SerializedName("is_deleted")
    val isDeleted: Boolean,
    @SerializedName("created_at")
    val createdAt: String
)
