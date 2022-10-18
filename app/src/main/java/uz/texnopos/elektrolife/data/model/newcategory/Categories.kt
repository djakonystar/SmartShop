package uz.texnopos.elektrolife.data.model.newcategory

import com.google.gson.annotations.SerializedName

data class Categories(
    @SerializedName("category_id")
    val categoryId: Int,
    val name: String,
    @SerializedName("min_count")
    val minQuantity: Int,
    @SerializedName("percent_wholesale")
    val wholesale: Int,
    @SerializedName("percent_min")
    val min: Int,
    @SerializedName("percent_max")
    val max: Int,
)
