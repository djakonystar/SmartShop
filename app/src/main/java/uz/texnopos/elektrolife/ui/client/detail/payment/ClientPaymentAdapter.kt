package uz.texnopos.elektrolife.ui.client.detail.payment

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.clients.ClientPayment
import uz.texnopos.elektrolife.databinding.ItemPaymentBinding

class ClientPaymentAdapter :
    BaseAdapter<ClientPayment, ClientPaymentAdapter.ClientPaymentViewHolder>() {
    inner class ClientPaymentViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(model: ClientPayment) {
            binding.apply {
                tvCash.text = itemView.context?.getString(R.string.sum_text, model.cash.toSumFormat)
                tvCard.text = itemView.context?.getString(R.string.sum_text, model.card.toSumFormat)
                tvVendor.text = model.vendor
                val createdDate = model.date.substring(0..9).changeDateFormat
                val createdTime = model.date.substring(11..18)
                tvDate.text = "$createdDate $createdTime"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientPaymentViewHolder {
        val itemView = parent.inflate(R.layout.item_payment)
        val binding = ItemPaymentBinding.bind(itemView)
        return ClientPaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientPaymentViewHolder, position: Int) {
        holder.populateModel(models[position])
    }
}
