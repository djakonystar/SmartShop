package uz.texnopos.elektrolife.ui.warehouse

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.Constants
import uz.texnopos.elektrolife.core.extensions.format
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.sumFormat
import uz.texnopos.elektrolife.data.model.warehouse.WarehouseItem
import uz.texnopos.elektrolife.databinding.ItemWarehouseBinding

class WarehouseAdapter : BaseAdapter<WarehouseItem, WarehouseAdapter.WarehouseViewHolder>() {
    inner class WarehouseViewHolder(private val binding: ItemWarehouseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(model: WarehouseItem) {
            binding.apply {
                tvName.text = model.product.name
                tvName.isSelected = true
                tvFirm.text = model.product.brand
                tvCategory.text = model.category.name
                if (model.unit.id == 1) {
                    tvCount.text = itemView.context?.getString(
                        R.string.price_text,
                        model.count.toLong().toString().sumFormat,
                        Constants.getUnitName(itemView.context, model.unit.id)
                    )
                } else {
                    tvCount.text = itemView.context?.getString(
                        R.string.price_text,
                        model.count.format(2).sumFormat,
                        Constants.getUnitName(itemView.context, model.unit.id)
                    )
                }
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
