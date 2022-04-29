package uz.texnopos.elektrolife.data.model.payment

data class PaymentHistory(
    val amount: Amount,
    val histories: List<Payment>
)
