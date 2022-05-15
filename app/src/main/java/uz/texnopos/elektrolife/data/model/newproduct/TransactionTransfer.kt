package uz.texnopos.elektrolife.data.model.newproduct

data class TransactionTransfer(
    val productId: Int,
    val productName: String,
    val count: Double,
    val unitId: Int,
    val price: Price
)
