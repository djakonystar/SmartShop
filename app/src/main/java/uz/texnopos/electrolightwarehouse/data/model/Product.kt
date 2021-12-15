package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_brand")
    val productBrand: String,
    @SerializedName("product_image")
    val productImage: String,
    @SerializedName("product_cost_price")
    val productCostPrice: Int,
    @SerializedName("price_wholesale")
    val priceWholesale: Long,
    @SerializedName("price_min")
    val priceMin: Long,
    @SerializedName("price_max")
    val priceMax: Long,
    var count: Int,
    var salePrice: Int
)