package uz.texnopos.elektrolife.data.model.sales.returnorder

import com.google.gson.annotations.SerializedName

data class ReturnOrder(
    @SerializedName("basket_id")
    val id: Int,
    @SerializedName("payment_type")
    val paymentType: List<String>,
    val orders: List<Order>
)
