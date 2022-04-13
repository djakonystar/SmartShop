package uz.texnopos.elektrolife.data.model.newclient

import com.google.gson.annotations.SerializedName

data class RegisterClient(
    @SerializedName("full_name")
    val name: String,
    val phone: String,
    @SerializedName("tin")
    val inn: String?,
    val about: String,
    @SerializedName("type")
    val clientType: String
)
