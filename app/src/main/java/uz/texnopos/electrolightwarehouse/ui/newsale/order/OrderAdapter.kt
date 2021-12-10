package uz.texnopos.electrolightwarehouse.ui.newsale.order

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.ItemOrderDialogBinding

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    var models: MutableList<Product> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun removeItem(position: Int){
        models.removeAt(position)
        models.clear()
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    var onItemClick: (product:Product) -> Unit = {_->}
    fun onItemClickListener(onItemClick: (product:Product) -> Unit) {
        this.onItemClick = onItemClick
    }

    inner class OrderViewHolder(private val binding: ItemOrderDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(model: Product, position: Int) {
            binding.tvProductName.text = model.productName
            binding.tvProductsCount.text = "(${model.count})"
            binding.tvPrice.text = (model.salePrice).changeFormat()
            binding.btnDelete.onClick {
                if (models.size>0){
                    removeItem(position)
                    onItemClick.invoke(model)
                }
            }
        }

    }

    private fun Int.changeFormat(): String {
        val num = this.toLong().toString()
        var s = ""
        val sz = num.length
        for (i in 0 until sz) {
            if (i != 0 && (i - sz % 3) % 3 == 0) s += ' '
            s += num[i]
        }
        return "$s uzs"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = parent.inflate(R.layout.item_order_dialog)
        val binding = ItemOrderDialogBinding.bind(itemView)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.populateModel(models[position], position)
    }

    override fun getItemCount() = models.size
}
