package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName
import uz.texnopos.elektrolife.core.extensions.saleEmployee
import uz.texnopos.elektrolife.data.model.sales.Amount
import uz.texnopos.elektrolife.data.model.sales.Customer
import uz.texnopos.elektrolife.data.model.sales.Debt
import uz.texnopos.elektrolife.data.model.sales.Order

data class BasketResponse(
    val id: Int,
    val amount: Amount,
    val term: String?,
    val description: String?,
    @SerializedName("user")
    val customer: Customer,
    val employee: saleEmployee,
    val orders: List<Order>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("qr_link")
    val qrLink: String
)
