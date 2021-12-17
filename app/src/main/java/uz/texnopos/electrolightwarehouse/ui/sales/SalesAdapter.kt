package uz.texnopos.electrolightwarehouse.ui.sales

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.BaseAdapter
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.toSumFormat
import uz.texnopos.electrolightwarehouse.data.model.sales.Sales
import uz.texnopos.electrolightwarehouse.databinding.ItemSalesBinding

class SalesAdapter : BaseAdapter<Sales, SalesAdapter.SalesViewHolder>() {
    inner class SalesViewHolder(private val binding: ItemSalesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(sales: Sales) {
            binding.apply {
                val basket = sales.basket
                tvClientName.text = sales.clientName
                tvTotalPrice.text = itemView.context?.getString(
                    R.string.sum_text,
                    basket.price.toLong().toSumFormat
                )
                if (basket.debt > 0) {
                    dot.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.red_eclipse
                        )
                    )
                    tvDebtPrice.text = itemView.context?.getString(
                        R.string.sum_text,
                        (-basket.debt.toLong()).toSumFormat
                    )
                } else {
                    dot.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.green_eclipse
                        )
                    )
                    tvDebtPrice.text = ""
                }
                tvSellerName.text = sales.vendorName
                tvDate.text = basket.createdAt
                if (basket.cash > 0 && basket.card > 0 && basket.debt > 0) {
                    tvPaymentInfo.text = itemView.context?.getString(
                        R.string.type_of_payment_three,
                        itemView.context?.getString(R.string.payment_cash),
                        itemView.context?.getString(R.string.payment_card),
                        itemView.context?.getString(R.string.payment_debt)
                    )
                } else if (basket.cash > 0 && basket.card > 0) {
                    tvPaymentInfo.text = itemView.context?.getString(
                        R.string.type_of_payment_two,
                        itemView.context?.getString(R.string.payment_cash),
                        itemView.context?.getString(R.string.payment_card)
                    )
                } else if (basket.cash > 0 && basket.debt > 0) {
                    tvPaymentInfo.text = itemView.context?.getString(
                        R.string.type_of_payment_two,
                        itemView.context?.getString(R.string.payment_cash),
                        itemView.context?.getString(R.string.payment_debt)
                    )
                } else if (basket.card > 0 && basket.debt > 0) {
                    tvPaymentInfo.text = itemView.context?.getString(
                        R.string.type_of_payment_two,
                        itemView.context?.getString(R.string.payment_card),
                        itemView.context?.getString(R.string.payment_debt)
                    )
                } else if (basket.cash > 0) {
                    tvPaymentInfo.text = itemView.context?.getString(R.string.payment_cash)
                } else if (basket.card > 0) {
                    tvPaymentInfo.text = itemView.context?.getString(R.string.payment_card)
                } else {
                    tvPaymentInfo.text = itemView.context?.getString(R.string.payment_debt)
                }
            }

            itemView.onClick {
                onClickItem.invoke(sales)
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

    private var onClickItem: (sales: Sales) -> Unit = {}
    fun onClickItem(clickItem: (sales: Sales) -> Unit) {
        this.onClickItem = clickItem
    }
}
