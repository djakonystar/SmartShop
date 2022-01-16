package uz.texnopos.elektrolife.data.model.finance

import com.google.gson.annotations.SerializedName

data class FinancePost(
    val description: String,
    val date: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("staff")
    val payee: String,
    val price: Double,
    val type: String
)
