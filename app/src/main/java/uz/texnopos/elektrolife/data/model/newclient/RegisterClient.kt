package uz.texnopos.elektrolife.data.model.newclient

import com.google.gson.annotations.SerializedName

data class RegisterClient(
    @SerializedName("full_name")
    val name: String,
    val phone: String,
    val inn: String?,
    val about: String,
    @SerializedName("user_type")
    val clientType: Int
)
