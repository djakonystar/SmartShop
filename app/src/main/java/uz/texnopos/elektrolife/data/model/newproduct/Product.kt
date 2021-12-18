package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("category_id")
    val categoryId: Int,
    val brand: String,
    val name: String,
    @SerializedName("cost_price")
    val costPrice: Int,
    @SerializedName("price_wholesale")
    val percentWholesale: Int,
    @SerializedName("price_min")
    val percentMin: Int,
    @SerializedName("price_max")
    val percentMax: Int,
    @SerializedName("new_count")
    val quantity: Int
)
