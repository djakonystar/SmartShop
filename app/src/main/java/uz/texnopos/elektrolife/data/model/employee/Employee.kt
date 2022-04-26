package uz.texnopos.elektrolife.data.model.employee

data class Employee(
    val id: Int,
    val name: String,
    val phone: String,
    val salary: Double?,
    val flex: Double?,
    val role: String
)
