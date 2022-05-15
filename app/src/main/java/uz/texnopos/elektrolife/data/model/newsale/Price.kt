package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName

data class Price(
    @SerializedName("currency_id")
    val currencyId: Int,
    val name: String,
    val code: String,
    val price: Double
)
