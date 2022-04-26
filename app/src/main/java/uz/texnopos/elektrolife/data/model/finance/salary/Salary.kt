package uz.texnopos.elektrolife.data.model.finance.salary

import uz.texnopos.elektrolife.data.model.employee.Employee

data class Salary(
    val employee: Employee,
    val sum: Double
)
