package uz.texnopos.elektrolife.data.model.sales

import com.google.gson.annotations.SerializedName
import uz.texnopos.elektrolife.core.extensions.saleCostumer
import uz.texnopos.elektrolife.core.extensions.saleEmployee

data class Basket(
    val id: Int,
    val card: Double,
    val cash: Double,
    val debt: Debt,
    val term: String?,
    val description: String?,
    @SerializedName("user")
    val customer: saleCostumer,
    val employee: saleEmployee,
    @SerializedName("qr_link")
    val qrLink: String,
    @SerializedName("created_at")
    val createdAt: String
)
