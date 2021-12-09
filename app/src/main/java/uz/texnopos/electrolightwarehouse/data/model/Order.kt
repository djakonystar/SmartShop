package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("client_id")
    val id:Int,
    @SerializedName("card")
    val card: Long,
    @SerializedName("cash")
    val cash: Long,
    @SerializedName("debt")
    val debt: Long,
    @SerializedName("price")
    val price:Double,
    @SerializedName("term")
    val term: String,
    @SerializedName("description")
    val description:String,
    @SerializedName("orders")
    val orders : List<OrderItem>
)