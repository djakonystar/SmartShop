package uz.texnopos.elektrolife.ui.warehouse

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.databinding.ItemWarehouseBinding

class WarehouseAdapter : BaseAdapter<newSaleProduct, WarehouseAdapter.WarehouseViewHolder>() {
    inner class WarehouseViewHolder(private val binding: ItemWarehouseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(product: newSaleProduct) {
            binding.apply {
                tvName.text = product.name
                tvName.isSelected = true
                tvFirm.text = product.brand
                val remained = product.warehouse?.count ?: 0.0
                val unitId = product.warehouse?.unit?.id ?: -1
                tvCount.text = itemView.context?.getString(
                    R.string.price_text,
                    if (unitId == 1) remained.toLong().toString().sumFormat
                    else remained.toString().sumFormat,
                    Constants.getUnitName(itemView.context, product.warehouse?.unit?.id ?: -1)
                )

                if (product.image != null) {
                    Glide.with(ivProduct)
                        .load(product.image)
                        .placeholder(R.drawable.image_placeholder)
                        .into(ivProduct)
                } else {
                    ivProduct.setImageResource(R.drawable.image_placeholder)
                }

                ivProduct.onClick {
                    product.image?.let { imageUrl ->
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

                itemView.onClick {
                    onItemClick(product)
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

    private var onItemClick: (product: newSaleProduct) -> Unit = {}
    fun setOnItemClickListener(onItemClick: (product: newSaleProduct) -> Unit) {
        this.onItemClick = onItemClick
    }
}
