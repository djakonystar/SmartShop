package uz.texnopos.elektrolife.data.model.signin

data class SignInPayload(
    val role: String,
    val name: String,
    val token: String
)
