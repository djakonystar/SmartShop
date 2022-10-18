package uz.texnopos.elektrolife.data.model.newcategory

import com.google.gson.annotations.SerializedName

data class Percent (
    val wholesale: Double,
    val min: Double,
    val max: Double,
    @SerializedName("min_product")
    val minProduct: Double
)