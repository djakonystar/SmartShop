package uz.texnopos.electrolightwarehouse.data.model.warehouse

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val name: String,
    @SerializedName("percent_wholesale")
    val percentWholesale: Int,
    @SerializedName("percent_min")
    val percentMin: Int,
    @SerializedName("percent_max")
    val percentMax: Int,
    @SerializedName("min_count")
    val minCount: Int
)
