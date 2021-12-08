package uz.texnopos.electrolightwarehouse.data.newCategory

data class NewCategory(
    val name: String,
    val wholesalePercent: Int,
    val maxPercent: Int,
    val minPercent: Int,
    val minQuantity: Int
)
