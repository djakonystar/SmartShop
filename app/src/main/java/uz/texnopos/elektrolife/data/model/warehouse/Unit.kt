package uz.texnopos.elektrolife.data.model.warehouse

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Unit(
    val id: Int,
    val name: String,
    val code: String
): Parcelable
