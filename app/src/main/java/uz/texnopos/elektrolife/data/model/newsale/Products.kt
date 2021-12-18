package uz.texnopos.elektrolife.data.model.newsale

import com.google.gson.annotations.SerializedName

data class Products(
    @SerializedName("current_page")
    val currentPage:Int,
    @SerializedName("last_page")
    val lastPage:Int,
    @SerializedName("products")
    val products: List<Product>
)