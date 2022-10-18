package uz.texnopos.elektrolife.data.model.sales

data class BasketResponse(
    val amount: Amount,
    val baskets: List<Basket>
)
