package uz.texnopos.elektrolife.ui.newsale.order

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ItemOrderDialogBinding

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    var models: MutableList<Product> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun removeItem(model: Product, position: Int) {
        models.remove(model)
        notifyItemRemoved(position)
    }

    var onItemClick: (product: Product, position: Int) -> Unit = { _, _ -> }
    fun onItemClickListener(onItemClick: (product: Product, position: Int) -> Unit) {
        this.onItemClick = onItemClick
    }

    inner class OrderViewHolder(private val binding: ItemOrderDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(model: Product, position: Int) {
            binding.apply {
                tvName.text = model.productName
                tvQuantity.text =
                    itemView.context?.getString(R.string.count_text, model.count.toSumFormat)
                tvCost.text =
                    itemView.context?.getString(R.string.sum_text, model.salePrice.toSumFormat)

                btnDelete.onClick {
                    if (models.size > 0) {
                        onItemClick.invoke(model, position)
                    }
                }
            }
        }
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
