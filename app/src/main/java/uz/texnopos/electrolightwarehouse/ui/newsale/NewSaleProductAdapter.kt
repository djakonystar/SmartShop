package uz.texnopos.electrolightwarehouse.ui.newsale

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.ItemNewSaleBinding

class NewSaleProductAdapter: RecyclerView.Adapter<NewSaleProductAdapter.NewSaleViewHolder>() {

    var models:List<Product> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var onItemClick: (product:Product) -> Unit = {}
    fun onItemClickListener(onItemClick: (product:Product) -> Unit) {
        this.onItemClick = onItemClick
    }

    inner class NewSaleViewHolder(private val binding:ItemNewSaleBinding) : RecyclerView.ViewHolder(binding.root){
        fun populateModel(product: Product){
            binding.apply {
                tvTitle.text = product.productName
                tvDescription.text = product.productBrand
                tvPrice.text = product.productCostPrice.toString()
            }
            binding.btnAddToBasket.onClick {
                onItemClick.invoke(product)
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
}