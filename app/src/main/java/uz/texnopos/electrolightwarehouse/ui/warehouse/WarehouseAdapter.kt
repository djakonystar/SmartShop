package uz.texnopos.electrolightwarehouse.ui.warehouse

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.BaseAdapter
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.data.model.Warehouse
import uz.texnopos.electrolightwarehouse.databinding.ItemWarehouseBinding

class WarehouseAdapter : BaseAdapter<Warehouse, WarehouseAdapter.WarehouseViewHolder>() {
    inner class WarehouseViewHolder(private val binding: ItemWarehouseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(model: Warehouse) {
            binding.apply {
                tvName.text = model.name
                tvCount.text =
                    itemView.context?.getString(R.string.count_text, model.remained.toString())
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
