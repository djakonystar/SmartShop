package uz.texnopos.electrolightwarehouse.ui.sales.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.BaseAdapter
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.core.extensions.toSumFormat
import uz.texnopos.electrolightwarehouse.data.model.sales.Product
import uz.texnopos.electrolightwarehouse.databinding.ItemSalesDetailBinding

class SalesDetailAdapter : BaseAdapter<Product, SalesDetailAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(private val binding: ItemSalesDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(product: Product) {
            binding.apply {
                tvProductName.text = product.productName
                tvBrand.text = product.productBrand
                tvCount.text = itemView.context?.getString(R.string.count_text, product.count.toSumFormat)
                tvCost.text = itemView.context?.getString(R.string.sum_text, product.price.toSumFormat)
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
