package uz.texnopos.elektrolife.data.model.warehouse

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val name: String?,
    @SerializedName("whole_percent")
    val wholePercent: Int,
    @SerializedName("min_percent")
    val minPercent: Int,
    @SerializedName("max_percent")
    val maxPercent: Int
)
