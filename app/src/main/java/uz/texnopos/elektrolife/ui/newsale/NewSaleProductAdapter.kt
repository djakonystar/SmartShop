package uz.texnopos.elektrolife.ui.newsale

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ItemNewSaleBinding

class NewSaleProductAdapter : RecyclerView.Adapter<NewSaleProductAdapter.NewSaleViewHolder>() {

    var models: List<Product> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClick: (product: Product) -> Unit = {}
    fun onItemClickListener(onItemClick: (product: Product) -> Unit) {
        this.onItemClick = onItemClick
    }

    inner class NewSaleViewHolder(private val binding: ItemNewSaleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(product: Product) {
            binding.apply {
                tvName.text = product.name
                tvName.isSelected = true
                tvBrand.text = product.brand
                tvCost.text = itemView.context?.getString(
                    R.string.price_text,
                    product.costPrice.price.toSumFormat,
                    product.costPrice.code
                )
                val remained = product.warehouse.count
                tvRemained.text = itemView.context?.getString(
                    R.string.count_text,
                    if (remained % 1 == 0.0) remained.toInt().toSumFormat
                    else remained.toSumFormat
                )

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

    override fun getItemCount(): Int = models.size

    fun filterList(filteredListName: MutableList<Product>) {
        models = filteredListName
        notifyDataSetChanged()
    }

}