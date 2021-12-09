package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class ProductInfo(
    @SerializedName("order_id")
    val orderId:Int,
    @SerializedName("product_id")
    val productId:Int,
    @SerializedName("product_brand")
    val productBrand:String,
    @SerializedName("product_name")
    val productName:String,
    @SerializedName("price")
    val price:Int,
    @SerializedName("count")
    val count:Int
)