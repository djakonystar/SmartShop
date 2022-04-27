package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName
import uz.texnopos.elektrolife.data.model.category.CategoryResponse

data class Product(
    val id: Int,
    val category: CategoryResponse,
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
    val warehouse: Warehouse?,
    @SerializedName("qr_code_link")
    val qrCode: String,
    var count: Double = 0.0,
    var salePrice: Double = 0.0
)