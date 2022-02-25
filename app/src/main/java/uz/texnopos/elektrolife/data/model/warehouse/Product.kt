package uz.texnopos.elektrolife.data.model.warehouse

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("product_id")
    val id: Int,
    @SerializedName("product_name")
    val name: String,
    @SerializedName("product_brand")
    val brand: String,
    val category: Category?,
    val remained: Int
)
