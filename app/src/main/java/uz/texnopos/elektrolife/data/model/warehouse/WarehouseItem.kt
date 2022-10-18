package uz.texnopos.elektrolife.data.model.warehouse

import uz.texnopos.elektrolife.core.extensions.warehouseProduct

data class WarehouseItem(
    val product: warehouseProduct,
    val category: Category,
    val count: Double,
    val unit: Unit,
    val date: String
)
