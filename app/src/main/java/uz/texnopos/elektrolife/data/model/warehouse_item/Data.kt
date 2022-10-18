package uz.texnopos.elektrolife.data.model.warehouse_item

data class Data(
    val brand: String,
    val category: Category,
    val cost_price: CostPrice,
    val deleted_at: Any,
    val id: Int,
    val image: String?,
    val max_price: MaxPrice,
    val min_price: MinPrice,
    val name: String,
    val qr_code_link: String,
    val warehouse: Warehouse,
    val whole_price: WholePrice
)