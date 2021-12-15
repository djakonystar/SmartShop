package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("client_id")
    val id:Int,
    @SerializedName("cash")
    val cash: Long,
    @SerializedName("card")
    val card: Long,
    @SerializedName("debt")
    val debt: Long,
    @SerializedName("price")
    val price:Long,
    @SerializedName("term")
    val term: String,
    @SerializedName("description")
    val description:String,
    @SerializedName("orders")
    val orders : List<OrderItem>
)