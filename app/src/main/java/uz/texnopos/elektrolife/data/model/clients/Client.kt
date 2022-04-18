package uz.texnopos.elektrolife.data.model.clients

import com.google.gson.annotations.SerializedName

data class Client(
    val id: Int,
    @SerializedName("full_name")
    val name: String,
    val phone: String,
    var tin: String? = null,
    val balance: Int?,
    @SerializedName("about")
    val comment: String? = "",
    @SerializedName("user_type")
    val type: String?
)
