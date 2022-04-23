package uz.texnopos.elektrolife.data.model.category

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    val id: Int,
    val name: String,
    @SerializedName("whole_percent")
    val wholePercent: Double,
    @SerializedName("min_percent")
    val minPercent: Double,
    @SerializedName("max_percent")
    val maxPercent: Double
)
