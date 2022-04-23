package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class Price(
    @SerializedName("currency_id")
    val currencyId: Int,
    val price: Double
)
