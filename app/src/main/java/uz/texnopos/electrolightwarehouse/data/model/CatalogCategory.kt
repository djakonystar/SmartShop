package uz.texnopos.electrolightwarehouse.data.model

data class CatalogCategory(
    val category_id: Int,
    val name: String,
    val percent_wholesale:Int,
    val percent_min:Int,
    val percent_max:Int,
    var selectedMood: Boolean = false
)
