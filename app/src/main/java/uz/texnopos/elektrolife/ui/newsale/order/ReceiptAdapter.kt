package uz.texnopos.elektrolife.ui.newsale.order

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.newSaleProduct
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.databinding.ItemReceiptBinding
import uz.texnopos.elektrolife.settings.Settings

class ReceiptAdapter(private val settings: Settings) :
    BaseAdapter<newSaleProduct, ReceiptAdapter.ReceiptViewHolder>() {
    inner class ReceiptViewHolder(private val binding: ItemReceiptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(product: newSaleProduct) {
            binding.apply {
                tvName.text = product.name
                tvCount.text = itemView.context?.getString(
                    R.string.receipt_count_text,
                    product.count,
                    product.salePrice.toSumFormat,
                    settings.currency
                )
                tvSum.text = itemView.context?.getString(
                    R.string.price_text,
                    (product.count * product.salePrice).toSumFormat,
                    settings.currency
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val itemView = parent.inflate(R.layout.item_receipt)
        val binding = ItemReceiptBinding.bind(itemView)
        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        holder.populateModel(models[position])
    }
}
