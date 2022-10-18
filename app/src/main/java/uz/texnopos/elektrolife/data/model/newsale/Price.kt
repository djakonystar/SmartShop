package uz.texnopos.elektrolife.data.model.newsale

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Price(
    @SerializedName("currency_id")
    val currencyId: Int,
    val name: String,
    val code: String,
    val price: Double
): Parcelable
