package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("category_id")
    val categoryId: Int,
    val brand: String,
    val name: String,
    @SerializedName("cost_price")
    val costPrice: Double,
    @SerializedName("price_wholesale")
    val wholesalePrice: Double,
    @SerializedName("price_min")
    val minPrice: Int,
    @SerializedName("price_max")
    val maxPrice: Int,
    @SerializedName("new_count")
    val quantity: Int
)
