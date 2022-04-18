package uz.texnopos.elektrolife.data.model

import com.google.gson.annotations.SerializedName

data class PagingResponse<T> (
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    val data: T
)
