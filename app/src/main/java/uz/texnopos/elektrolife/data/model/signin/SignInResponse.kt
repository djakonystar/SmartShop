package uz.texnopos.elektrolife.data.model.signin

data class SignInResponse(
    val role: String,
    val name: String,
    val token: String
)
