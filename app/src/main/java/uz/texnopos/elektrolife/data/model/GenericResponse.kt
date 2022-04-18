package uz.texnopos.elektrolife.data.model

data class GenericResponse<T> (
    val successful: Boolean,
    val code: Int,
    val message: String? = "",
    val payload: T
)