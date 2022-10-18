package uz.texnopos.elektrolife.ui.client.detail.payment

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.payment.Payment
import uz.texnopos.elektrolife.databinding.ItemPaymentBinding
import uz.texnopos.elektrolife.settings.Settings

class ClientPaymentAdapter(private val settings: Settings) :
    BaseAdapter<Payment, ClientPaymentAdapter.PaymentViewHolder>() {
    inner class PaymentViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(payment: Payment) {
            binding.apply {
                tvClient.text = payment.client.name
                tvCash.isSelected = true
                tvCard.isSelected = true
                tvVendor.isSelected = true
                tvCash.text = itemView.context?.getString(
                    R.string.price_text,
                    payment.amount.cash.toSumFormat,
                    settings.currency
                )
                tvCard.text = itemView.context?.getString(
                    R.string.price_text,
                    payment.amount.card.toSumFormat,
                    settings.currency
                )
                tvVendor.text = payment.employee.name
                val createdDate = payment.time.substring(0..9).changeDateFormat
                val createdTime = payment.time.substring(11..18)
                tvDate.text = "$createdDate $createdTime"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val itemView = parent.inflate(R.layout.item_payment)
        val binding = ItemPaymentBinding.bind(itemView)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.populateModel(models[position])
    }
}
