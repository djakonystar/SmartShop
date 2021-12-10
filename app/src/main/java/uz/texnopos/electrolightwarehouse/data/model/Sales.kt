package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class Sales(
    @SerializedName("client_id")
    val clientId:Int,
    @SerializedName("client_name")
    val clientName:String,
    @SerializedName("phone")
    val phone:String,
    @SerializedName("basket")
    val basket:Basket,
    @SerializedName("orders")
    val orders:List<ProductInfo>,
    @SerializedName("vendor_name")
    val vendorName:String
)
