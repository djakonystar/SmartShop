package uz.texnopos.elektrolife.data.model.qrcode

import uz.texnopos.elektrolife.data.model.newsale.Warehouse

data class ProductResponse(
    val product: Product,
    val category: Category,
    val warehouse: Warehouse?
)