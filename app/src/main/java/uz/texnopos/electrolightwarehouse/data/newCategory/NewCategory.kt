package uz.texnopos.electrolightwarehouse.data.newCategory

import com.google.gson.annotations.SerializedName

data class NewCategory(
    val name: String,
    @SerializedName("min_count")
    val minQuantity: Int,
    val percents: Percent
)
