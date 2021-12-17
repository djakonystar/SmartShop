package uz.texnopos.electrolightwarehouse.data.model.newsale

import com.google.gson.annotations.SerializedName

data class CatalogCategory(
    @SerializedName("category_id")
    val id: Int,
    val name: String,
    @SerializedName("percent_wholesale")
    val percentWholesale: Int,
    @SerializedName("percent_min")
    val percentMin: Int,
    @SerializedName("percent_max")
    val percentMax: Int
)
