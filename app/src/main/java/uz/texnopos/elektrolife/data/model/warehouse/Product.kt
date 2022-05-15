package uz.texnopos.elektrolife.data.model.warehouse

import com.google.gson.annotations.SerializedName
import uz.texnopos.elektrolife.data.model.newproduct.Price

data class Product(
    val id: Int,
    val name: String,
    val brand: String,
    val image: String?,
    @SerializedName("cost_price")
    val costPrice: Price
)
