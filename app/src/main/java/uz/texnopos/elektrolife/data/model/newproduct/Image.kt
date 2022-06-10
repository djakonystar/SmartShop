package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("secure_url")
    val secureUrl: String
)
