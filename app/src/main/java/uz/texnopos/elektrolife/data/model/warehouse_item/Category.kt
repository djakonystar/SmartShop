package uz.texnopos.elektrolife.data.model.warehouse_item

data class Category(
    val id: Int,
    val max_percent: Int,
    val min_percent: Int,
    val min_product: Int,
    val whole_percent: Int
)