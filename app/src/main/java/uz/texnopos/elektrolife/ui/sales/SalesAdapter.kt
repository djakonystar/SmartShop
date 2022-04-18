package uz.texnopos.elektrolife.ui.sales

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.databinding.ItemSalesBinding
import uz.texnopos.elektrolife.settings.Settings

class SalesAdapter(private val settings: Settings) :
    BaseAdapter<Basket, SalesAdapter.SalesViewHolder>() {
    inner class SalesViewHolder(private val binding: ItemSalesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(basket: Basket) {
            binding.apply {
                val totalPrice = basket.cash + basket.card + basket.debt.debt
                tvCardPrice.isSelected = true
                tvCashPrice.isSelected = true
                tvDebtPrice.isSelected = true
                tvClientName.text = basket.customer.name
                tvPhone.text = basket.customer.phone.toPhoneFormat
                tvTotalPrice.text = itemView.context?.getString(
                    R.string.price_text,
                    totalPrice.checkModule.toSumFormat,
                    settings.currency
                )
                tvCashPrice.text = itemView.context?.getString(
                    R.string.price_text,
                    basket.cash.checkModule.toSumFormat,
                    settings.currency
                )
                tvCardPrice.text = itemView.context?.getString(
                    R.string.price_text,
                    basket.card.checkModule.toSumFormat,
                    settings.currency
                )
                if (basket.debt.debt > 0) {
                    dot.setImageResource(R.drawable.red_eclipse)
                    tvDebtPrice.text = itemView.context?.getString(
                        R.string.price_text,
                        "-${basket.debt.debt.checkModule.toSumFormat}",
                        settings.currency
                    )
                } else {
                    dot.setImageResource(R.drawable.green_eclipse)
                    tvDebtPrice.text = itemView.context?.getString(
                        R.string.price_text,
                        basket.debt.debt.checkModule.toSumFormat,
                        settings.currency
                    )
//                    tvDebtPrice.text = ""
                }
                tvSellerName.text = basket.employee.name
                val createdDate = basket.createdAt.substring(0..9).changeDateFormat
                val createdTime = basket.createdAt.substring(11..18)
                tvDate.text = "$createdDate $createdTime"
            }

            itemView.onClick {
                onClickItem.invoke(basket)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val itemView = parent.inflate(R.layout.item_sales)
        val binding = ItemSalesBinding.bind(itemView)
        return SalesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        holder.populateModel(models[position])
    }

    private var onClickItem: (basket: Basket) -> Unit = {}
    fun onClickItem(clickItem: (basket: Basket) -> Unit) {
        this.onClickItem = clickItem
    }
}
