package uz.texnopos.elektrolife.data.model.sales

import com.google.gson.annotations.SerializedName
import uz.texnopos.elektrolife.core.extensions.saleCostumer

data class OrderResponse(
    val id: Int,
    @SerializedName("user")
    val costumer: saleCostumer,
    val amount: Amount,
    val orders: List<Order>
)