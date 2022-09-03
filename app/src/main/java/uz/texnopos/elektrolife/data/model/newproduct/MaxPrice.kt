package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class MaxPrice(
    val price: Double,
    @SerializedName("currency_id")
    val currencyId: Int
)