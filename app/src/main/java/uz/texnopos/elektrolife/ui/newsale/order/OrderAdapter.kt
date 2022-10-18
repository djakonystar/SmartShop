package uz.texnopos.elektrolife.ui.newsale.order

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ItemOrderDialogBinding
import uz.texnopos.elektrolife.settings.Settings

class OrderAdapter(private val settings: Settings) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    var models: MutableList<Product> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class OrderViewHolder(private val binding: ItemOrderDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(model: Product, position: Int) {
            binding.apply {
                tvName.text = model.name
                val unitId = model.warehouse?.unit?.id ?: 1
                tvQuantity.text = itemView.context?.getString(
                    R.string.price_text,
                    if (unitId == 1) model.count.toLong().toSumFormat else model.count.toSumFormat,
                    Constants.getUnitName(root.context, unitId)
                )
                tvCost.text = itemView.context?.getString(
                    R.string.price_text,
                    model.salePrice.toSumFormat,
                    settings.currency
                )

                plusQuantity.onClick {
                    onPlusCount.invoke(model)
                }

                minusQuantity.onClick {
                    onMinusCount.invoke(model)
                }

                btnEdit.onClick {
                    onEditClick(model, position)
                }

                btnDelete.onClick {
                    if (models.size > 0) {
                        onDeleteItem.invoke(model, position)
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

    private var onDeleteItem: (product: Product, position: Int) -> Unit = { _, _ -> }
    fun onDeleteItemClickListener(onDeleteItem: (product: Product, position: Int) -> Unit) {
        this.onDeleteItem = onDeleteItem
    }

    private var onEditClick: (product: Product, position: Int) -> Unit = { _, _ -> }
    fun setOnEditClickListener(onEditClick: (product: Product, position: Int) -> Unit) {
        this.onEditClick = onEditClick
    }

    private var onPlusCount: (product: Product) -> Unit = {}
    fun onPlusCounterClickListener(onPlusCount: (product: Product) -> Unit) {
        this.onPlusCount = onPlusCount
    }

    private var onMinusCount: (product: Product) -> Unit = {}
    fun onMinusCounterClickListener(onMinusCount: (product: Product) -> Unit) {
        this.onMinusCount = onMinusCount
    }

    fun plusCount(model: Product) {
        val position = models.indexOf(model)
//        models[position].count++
        notifyItemChanged(position)
    }

    fun minusCount(model: Product) {
        val position = models.indexOf(model)
//        models[position].count--
        notifyItemChanged(position)
    }

    fun removeItem(model: Product, position: Int, action: (models: MutableList<Product>) -> Unit) {
        val products = models
        products.removeAt(position)
        action(products)
    }
}
