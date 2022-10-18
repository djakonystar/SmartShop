package uz.texnopos.elektrolife.data.model.qrcode

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    @SerializedName("whole_percent")
    val wholesalePercent: Double,
    @SerializedName("min_percent")
    val minPercent: Double,
    @SerializedName("max_percent")
    val maxPercent: Double
)
