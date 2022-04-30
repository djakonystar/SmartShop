package uz.texnopos.elektrolife.data.model.finance

import com.google.gson.annotations.SerializedName

data class Finance(
    val description: String,
    val date: String,
    @SerializedName("category_name")
    val category: Category,
    @SerializedName("whom")
    val payee: String,
    val price: Double,
    @SerializedName("paymet_type")
    val paymentType: String
)
