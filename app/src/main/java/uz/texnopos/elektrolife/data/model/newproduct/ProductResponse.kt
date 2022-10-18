package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val id: Int,
    @SerializedName("category_id")
    val categoryId: Int,
    val image: String?,
    val name: String,
    val brand: String,
    @SerializedName("cost_price")
    val costPrice: Price,
    @SerializedName("whole_price")
    val wholesalePrice: Price,
    @SerializedName("min_price")
    val minPrice: Price,
    @SerializedName("max_price")
    val maxPrice: Price,

)
