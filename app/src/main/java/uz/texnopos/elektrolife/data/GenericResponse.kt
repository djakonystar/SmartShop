package uz.texnopos.elektrolife.data

data class GenericResponse<T> (
    val successful: Boolean,
    val code: Int,
    val message: String? = "",
    val payload: T
)