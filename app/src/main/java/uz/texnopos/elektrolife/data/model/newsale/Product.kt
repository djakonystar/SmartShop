package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val category: CatalogCategory,
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
    val maxPrice: Price,
    val warehouse: Warehouse,
    @SerializedName("qr_code_name")
    val qrCode: String,
    var count: Int,
    var salePrice: Double
)