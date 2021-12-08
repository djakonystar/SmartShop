package uz.texnopos.electrolightwarehouse.data

import com.google.gson.annotations.SerializedName

data class ClientInfo(
    @SerializedName("client_id")
    val clientId: Int,
    @SerializedName("full_name")
    val name: String,
    val phone: String,
    @SerializedName("iin")
    val inn: String?,
    val balance: Int?,
    val about: String,
    @SerializedName("user_type")
    val userType: Boolean,
    @SerializedName("created_at")
    val createdAt: String

)
