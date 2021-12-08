package uz.texnopos.electrolightwarehouse.data.newProduct

import com.google.gson.annotations.SerializedName

data class Categories(
    @SerializedName("category_id")
    val categoryId: Int,
    val name: String,
    @SerializedName("percent_wholesale")
    val percentWholesale: Int =15,
    @SerializedName("percent_min")
    val percentMin: Int = 10,
    @SerializedName("percent_max")
    val percentMax: Int = 20
)
