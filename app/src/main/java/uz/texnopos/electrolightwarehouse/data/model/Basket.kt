package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class Basket(
    @SerializedName("id")
    val id:Int,
    @SerializedName("card")
    val card:Double,
    @SerializedName("cash")
    val cash:Double,
    @SerializedName("debt")
    val debt:Double,
    @SerializedName("price")
    val price:Double,
    @SerializedName("term")
    val term: String,
    @SerializedName("description")
    val description:String,
    @SerializedName("is_deleted")
    val isDeleted:Boolean,
    @SerializedName("created_at")
    val createdAt:String
)
