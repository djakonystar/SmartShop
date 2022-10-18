package uz.texnopos.elektrolife.data.model.newcategory

import com.google.gson.annotations.SerializedName

data class NewCategory(
    val name: String,
    @SerializedName("min_count")
    val minQuantity: Int,
    val percents: Percent
)
