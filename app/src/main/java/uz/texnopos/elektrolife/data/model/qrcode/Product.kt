package uz.texnopos.elektrolife.data.model.qrcode

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val name: String,
    val brand: String,
    val image: String?,
    @SerializedName("cost_price")
    val costPrice: Price,
    @SerializedName("whole_price")
    val wholesalePrice: Price,
    @SerializedName("min_price")
    val minPrice: Price,
    @SerializedName("max_price")
    val maxPrice: Price
)
