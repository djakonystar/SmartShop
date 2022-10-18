package uz.texnopos.elektrolife.data.model.sales

import com.google.gson.annotations.SerializedName

data class Amount(
    val cash: Double,
    val card: Double,
    val debt: Double,
    @SerializedName("paid_debt")
    val paidDebt: Double,
    val remaining: Double,
    val sum: Double = 0.0
)
