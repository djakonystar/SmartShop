package uz.texnopos.elektrolife.ui.newsale

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ItemNewSaleBinding

class NewSaleProductAdapter : BaseAdapter<Product, NewSaleProductAdapter.NewSaleViewHolder>() {
    inner class NewSaleViewHolder(private val binding: ItemNewSaleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(product: Product) {
            binding.apply {
                tvName.text = product.name
                tvName.isSelected = true
                tvBrand.text = product.brand
                val remained = product.warehouse?.count ?: 0.0
                val unitId = product.warehouse?.unit?.id ?: -1
                tvRemained.text = itemView.context?.getString(
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
                    onImageClick(product, ivProduct)
                }

                itemView.onClick {
                    onItemClick.invoke(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewSaleViewHolder {
        val itemView = parent.inflate(R.layout.item_new_sale)
        val binding = ItemNewSaleBinding.bind(itemView)
        return NewSaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewSaleViewHolder, position: Int) {
        holder.populateModel(models[position])
    }

    var onItemClick: (product: Product) -> Unit = {}
    fun onItemClickListener(onItemClick: (product: Product) -> Unit) {
        this.onItemClick = onItemClick
    }

    var onImageClick: (product: newSaleProduct, imageView: ImageView) -> Unit = { _, _ -> }
    fun onImageClickListener(onImageClick: (product: newSaleProduct, imageView: ImageView) -> Unit) {
        this.onImageClick = onImageClick
    }
}
