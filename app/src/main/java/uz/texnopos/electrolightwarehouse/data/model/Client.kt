package uz.texnopos.electrolightwarehouse.data.model

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("client_id")
    val id: Int,
    @SerializedName("full_name")
    val name: String,
    val phone: String,
    @SerializedName("inn")
    var tin: Int? = null,
    val balance: Int?,
    @SerializedName("about")
    val comment: String = "",
    @SerializedName("user_type")
    val type: Int
)
