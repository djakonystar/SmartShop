package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("client_id")
    val id: Int,
    val cash: Long,
    val card: Long,
    val debt: Long,
    val price: Long,
    val term: String,
    val description: String,
    val orders: List<OrderItem>
)