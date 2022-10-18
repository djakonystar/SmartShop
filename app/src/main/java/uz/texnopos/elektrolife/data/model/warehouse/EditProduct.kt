package uz.texnopos.elektrolife.data.model.warehouse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import uz.texnopos.elektrolife.data.model.newproduct.Price

@Parcelize
data class EditProduct(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("category_id")
    val categoryId: Int,
    val name: String,
    val brand: String,
    @SerializedName("cost_price")
    val costPrice: Price,
    @SerializedName("price_wholesale")
    val wholesalePrice: Price,
    @SerializedName("price_min")
    val minPrice: Price,
    @SerializedName("price_max")
    val maxPrice: Price,
    val image: String = ""
): Parcelable
