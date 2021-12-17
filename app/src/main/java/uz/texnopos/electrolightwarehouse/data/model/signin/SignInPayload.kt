package uz.texnopos.electrolightwarehouse.data.model.signin

data class SignInPayload(
    val role: String,
    val name: String,
    val token: String
)
