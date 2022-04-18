package uz.texnopos.elektrolife.ui.sales.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.checkModule
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.core.extensions.unitConverter
import uz.texnopos.elektrolife.data.model.sales.Order
import uz.texnopos.elektrolife.data.model.sales.Product
import uz.texnopos.elektrolife.databinding.ItemSalesDetailBinding
import uz.texnopos.elektrolife.settings.Settings

class SalesDetailAdapter(private val settings: Settings) :
    BaseAdapter<Order, SalesDetailAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(private val binding: ItemSalesDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(order: Order) {
            binding.apply {
                tvProductName.text = order.productName
                tvBrand.text = order.brand
                tvCount.text = itemView.context?.getString(
                    R.string.price_text,
                    order.count.checkModule.toSumFormat,
                    order.unitId.unitConverter(tvCount.context)
                )
                tvCost.text = itemView.context?.getString(
                    R.string.price_text,
                    order.price.checkModule.toSumFormat,
                    settings.currency
                )

                val total = order.count * order.price
                tvTotalPrice.text = itemView.context?.getString(
                    R.string.price_text,
                    total.checkModule.toSumFormat,
                    settings.currency
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val itemView = parent.inflate(R.layout.item_sales_detail)
        val binding = ItemSalesDetailBinding.bind(itemView)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.populateModel(models[position])
    }
}
