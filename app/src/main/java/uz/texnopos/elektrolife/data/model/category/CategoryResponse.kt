package uz.texnopos.elektrolife.data.model.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryResponse(
    val id: Int,
    val name: String,
    @SerializedName("whole_percent")
    val wholePercent: Double,
    @SerializedName("min_percent")
    val minPercent: Double,
    @SerializedName("max_percent")
    val maxPercent: Double,
    @SerializedName("min_product")
    val minProduct: Double? = 0.0
): Parcelable
