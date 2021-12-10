package uz.texnopos.electrolightwarehouse.ui.sales.detail

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.BaseAdapter
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.data.model.ProductInfo
import uz.texnopos.electrolightwarehouse.databinding.ItemSalesDetailBinding

class SalesDetailAdapter : BaseAdapter<ProductInfo, SalesDetailAdapter.DetailViewHolder>() {

    var allModel: List<ProductInfo> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            models = value
            notifyDataSetChanged()
        }

    inner class DetailViewHolder(private val binding: ItemSalesDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(productInfo: ProductInfo) {
            binding.apply {
                tvProductName.text = productInfo.productName
                tvBrand.text = productInfo.productBrand
                tvCount.text = productInfo.count.toString()
                tvCost.text = "${productInfo.price} uzs"
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

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredListName: MutableList<ProductInfo>) {
        models = filteredListName
        notifyDataSetChanged()
    }
}
