package uz.texnopos.electrolightwarehouse.data.model.clients

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("client_id")
    val id: Int,
    @SerializedName("full_name")
    val name: String,
    val phone: String,
    @SerializedName("inn")
    var tin: String? = null,
    val balance: Int?,
    @SerializedName("about")
    val comment: String = "",
    @SerializedName("user_type")
    val type: Int
)
