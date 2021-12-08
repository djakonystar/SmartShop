package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class Warehouse(
    @SerializedName("product_id")
    val id: Int,
    @SerializedName("product_name")
    val name: String,
    val remained: Int
)
