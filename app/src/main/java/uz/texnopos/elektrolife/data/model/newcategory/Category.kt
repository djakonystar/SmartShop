package uz.texnopos.elektrolife.data.model.newcategory

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    @SerializedName("parent_id")
    val parentId: Int,
    val name: String,
    @SerializedName("whole_percent")
    val wholePercent: Double,
    @SerializedName("min_percent")
    val minPercent: Double,
    @SerializedName("max_percent")
    val maxPercent: Double
)
