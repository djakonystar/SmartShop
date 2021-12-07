package uz.texnopos.electrolightwarehouse.data.model

data class ClientResponse(
    val name: String,
    val phone: String,
    var tin: Int = 0,
    var address: String = "",
    val balance: Long,
    var comment: String = "",
    val type: Int
)
