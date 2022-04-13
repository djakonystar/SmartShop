package uz.texnopos.elektrolife.data.model.currency

data class Currency(
    val id: Int,
    val name: String,
    val code: String,
    val rate: List<Rate>
)
