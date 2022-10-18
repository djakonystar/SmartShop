package uz.texnopos.elektrolife.data.model.finance

import uz.texnopos.elektrolife.data.model.payment.Amount

data class FinanceResponse(
    val amount: Amount,
    val items: List<Finance>
)
