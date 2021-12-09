package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("product_id")
    var productId:Int,
    @SerializedName("count")
    var count:Int,
    @SerializedName("price")
    var price: Long
)