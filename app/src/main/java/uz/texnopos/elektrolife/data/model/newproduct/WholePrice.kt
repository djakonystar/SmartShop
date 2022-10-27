package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class WholePrice(
    val price: Double,
    @SerializedName("currency_id")
    val currencyId: Int
)