package uz.texnopos.elektrolife.data.model.newsale

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import uz.texnopos.elektrolife.data.model.warehouse.Unit

@Parcelize
data class Warehouse(
    val unit: Unit,
    val count: Double?
): Parcelable
