package uz.texnopos.elektrolife.ui.sales.detail

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.checkModule
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.sales.Order
import uz.texnopos.elektrolife.databinding.ItemReceiptBinding
import uz.texnopos.elektrolife.settings.Settings

class OrderReceiptAdapter(private val settings: Settings) :
    BaseAdapter<Order, OrderReceiptAdapter.OrderReceiptViewHolder>() {
    inner class OrderReceiptViewHolder(private val binding: ItemReceiptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(order: Order, position: Int) {
            binding.apply {
                tvName.text = "${position + 1}. ${order.productName}"
                tvCount.text = "${order.count.checkModule}x${order.price.toSumFormat} ${settings.currency}"
                tvSum.text = itemView.context?.getString(
                    R.string.price_text,
                    (order.count * order.price).toSumFormat,
                    settings.currency
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderReceiptViewHolder {
        val itemView = parent.inflate(R.layout.item_receipt)
        val binding = ItemReceiptBinding.bind(itemView)
        return OrderReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderReceiptViewHolder, position: Int) {
        holder.populateModel(models[position], position)
    }
}
