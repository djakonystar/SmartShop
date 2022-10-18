package uz.texnopos.elektrolife.data.model.warehouse_item

data class Payload(
    val current_page: Int,
    val `data`: List<Data>,
    val last_page: Int,
    val per_page: Int
)