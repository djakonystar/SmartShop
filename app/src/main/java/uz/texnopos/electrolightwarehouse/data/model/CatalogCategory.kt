package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class CatalogCategory(
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("percent_wholesale")
    val percentWholesale:Int,
    @SerializedName("percent_min")
    val percentMin:Int,
    @SerializedName("percent_max")
    val percent_max:Int,
    var selectedMood: Boolean = false
)
