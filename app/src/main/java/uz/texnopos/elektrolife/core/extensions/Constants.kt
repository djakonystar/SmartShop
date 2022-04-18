package uz.texnopos.elektrolife.core.extensions

import android.content.Context

object Constants {
    fun getUnitName(context: Context, unitId: Int): String {
        var unitName = ""
        when (unitId) {
            1 -> unitName = "dona"
            2 -> unitName = "tonna"
            3 -> unitName = "kg"
            4 -> unitName = "gr"
            5 -> unitName = "metr"
            6 -> unitName = "sm"
            7 -> unitName = "litr"
        }
        return unitName
    }
}
