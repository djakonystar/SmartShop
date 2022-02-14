package uz.texnopos.elektrolife.data.model.warehouse

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val name: String? = "",
    @SerializedName("min_count")
    val minCount: Int?
)
