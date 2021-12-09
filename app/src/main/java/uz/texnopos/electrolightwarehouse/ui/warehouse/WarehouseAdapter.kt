package uz.texnopos.electrolightwarehouse.ui.warehouse

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.BaseAdapter
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.core.extensions.toSumFormat
import uz.texnopos.electrolightwarehouse.data.model.warehouse.WarehouseProduct
import uz.texnopos.electrolightwarehouse.databinding.ItemWarehouseBinding

class WarehouseAdapter : BaseAdapter<WarehouseProduct, WarehouseAdapter.WarehouseViewHolder>() {
    inner class WarehouseViewHolder(private val binding: ItemWarehouseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(model: WarehouseProduct) {
            binding.apply {
                tvName.text = model.name
                tvFirm.text = model.brand
                tvCategory.text = model.category.name
                tvCost.text = itemView.context?.getString(R.string.sum_text, model.costPrice.toSumFormat)
                tvWholesalePercent.text = "${model.category.percentWholesale}%"
                tvMinimumPercent.text = "${model.category.percentMin}%"
                tvMaximumPercent.text = "${model.category.percentMax}%"
                tvCount.text = itemView.context?.getString(R.string.count_text, model.remained.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarehouseViewHolder {
        val itemView = parent.inflate(R.layout.item_warehouse)
        val binding = ItemWarehouseBinding.bind(itemView)
        return WarehouseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WarehouseViewHolder, position: Int) {
        holder.populateModel(models[position])
    }
}
