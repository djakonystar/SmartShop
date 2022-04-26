package uz.texnopos.elektrolife.data.model.finance

import com.google.gson.annotations.SerializedName

data class FinancePost(
    @SerializedName("category_id")
    val categoryId: Int,
    val date: String,
    val price: Double,
    val description: String,
    val type: String,
    @SerializedName("whom")
    val payee: String,
    @SerializedName("payment_type")
    val paymentType: String
)
