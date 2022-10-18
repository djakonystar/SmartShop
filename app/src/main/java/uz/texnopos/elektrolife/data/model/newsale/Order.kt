package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("client_id")
    val id: Int,
    val cash: Double,
    val card: Double,
    val debt: Double,
    val term: String,
    val description: String,
    val orders: List<OrderItem>
)