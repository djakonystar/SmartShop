package uz.texnopos.elektrolife.ui.warehouse

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.*
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

                if (model.product.image != null) {
                    Glide.with(ivProduct)
                        .load(model.product.image)
                        .placeholder(R.drawable.image_placeholder)
                        .into(ivProduct)
                } else {
                    ivProduct.setImageResource(R.drawable.image_placeholder)
                }

                ivProduct.onClick {
                    model.product.image?.let { imageUrl ->
                        StfalconImageViewer.Builder(itemView.context, arrayOf(imageUrl)) { view, url ->
                            Glide.with(itemView.context)
                                .load(url)
                                .placeholder(R.drawable.image_placeholder)
                                .into(view)
                        }
                            .allowSwipeToDismiss(true)
                            .withTransitionFrom(ivProduct)
                            .show()
                    }
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
