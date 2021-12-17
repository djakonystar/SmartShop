package uz.texnopos.electrolightwarehouse.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class Categories(
    @SerializedName("category_id")
    val categoryId: Int,
    val name: String,
    @SerializedName("percent_wholesale")
    val percentWholesale: Int,
    @SerializedName("percent_min")
    val percentMin: Int ,
    @SerializedName("percent_max")
    val percentMax: Int
)
