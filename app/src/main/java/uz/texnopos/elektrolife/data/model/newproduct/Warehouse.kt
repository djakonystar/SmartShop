package uz.texnopos.elektrolife.data.model.newproduct

import com.google.gson.annotations.SerializedName

data class Warehouse(
    @SerializedName("unit_id")
    val unitId: Int,
    val count: Double
)
