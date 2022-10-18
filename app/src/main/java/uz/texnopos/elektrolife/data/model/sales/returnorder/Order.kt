package uz.texnopos.elektrolife.data.model.sales.returnorder

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("order_id")
    val id: Int,
    val count: Double
)
