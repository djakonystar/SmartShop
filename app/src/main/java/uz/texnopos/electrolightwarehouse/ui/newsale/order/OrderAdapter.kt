package uz.texnopos.electrolightwarehouse.ui.newsale.order

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.ItemOrderDialogBinding

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    var models: List<Product> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class OrderViewHolder(private val binding: ItemOrderDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(model: Product) {
            binding.tvProductName.text = model.product_name
            binding.tvProductsCount.text = "(${model.count})"
            binding.tvPrice.text = (model.price_max * model.count).toString() + " uzs"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = parent.inflate(R.layout.item_order_dialog)
        val binding = ItemOrderDialogBinding.bind(itemView)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.populateModel(models[position])
    }

    override fun getItemCount() = models.size
}
