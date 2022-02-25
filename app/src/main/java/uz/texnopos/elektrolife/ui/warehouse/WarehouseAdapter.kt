package uz.texnopos.elektrolife.ui.warehouse

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.data.model.warehouse.Product
import uz.texnopos.elektrolife.databinding.ItemWarehouseBinding

class WarehouseAdapter : BaseAdapter<Product, WarehouseAdapter.WarehouseViewHolder>() {
    inner class WarehouseViewHolder(private val binding: ItemWarehouseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(model: Product) {
            binding.apply {
                tvName.text = model.name
                tvName.isSelected = true
                tvFirm.text = model.brand
                tvCategory.text = model.category?.name ?: ""
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
