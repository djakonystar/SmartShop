package uz.texnopos.elektrolife.data.model.clients

data class ClientResponse(
    val debt: Double,
    val clients: List<Client>
)
