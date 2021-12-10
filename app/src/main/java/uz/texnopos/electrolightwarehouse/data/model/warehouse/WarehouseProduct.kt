package uz.texnopos.electrolightwarehouse.data.model.warehouse

import com.google.gson.annotations.SerializedName

data class WarehouseProduct(
    @SerializedName("product_id")
    val id: Int,
    @SerializedName("product_name")
    val name: String,
    @SerializedName("product_brand")
    val brand: String,
    @SerializedName("product_cost_price")
    val costPrice: Int,
    val category: Category,
    val remained: Int
)
