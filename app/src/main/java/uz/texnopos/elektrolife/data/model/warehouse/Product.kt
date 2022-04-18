package uz.texnopos.elektrolife.data.model.warehouse

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val name: String,
    val brand: String,
    val image: String?
)
