package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("category_id")
    val categoryId: Int,
    val name: String,
    val brand: String,
    @SerializedName("cost_price")
    val costPrice: Price,
    @SerializedName("price_wholesale")
    val wholesalePrice: Price,
    @SerializedName("price_min")
    val minPrice: Price,
    @SerializedName("price_max")
    val maxPrice: Price,
    val warehouse: Warehouse,
    val image: String = ""
)
