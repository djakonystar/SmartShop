package uz.texnopos.elektrolife.data.model.signin

data class SignInResponse(
    val id: Int,
    val role: String,
    val phone: String,
    val name: String,
    val token: String
)
