package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName

data class CatalogCategory(
    @SerializedName("category_id")
    val id: Int,
    val name: String,
    @SerializedName("whole_percent")
    val percentWholesale: Double,
    @SerializedName("min_percent")
    val percentMin: Double,
    @SerializedName("max_percent")
    val percentMax: Double
)
