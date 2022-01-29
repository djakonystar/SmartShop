package uz.texnopos.elektrolife.ui.sales

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.sales.Sales
import uz.texnopos.elektrolife.databinding.ItemSalesBinding

class SalesAdapter : BaseAdapter<Sales, SalesAdapter.SalesViewHolder>() {
    inner class SalesViewHolder(private val binding: ItemSalesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(sales: Sales) {
            binding.apply {
                val basket = sales.basket
                tvClientName.text = sales.clientName
                tvTotalPrice.text = itemView.context?.getString(
                    R.string.sum_text,
                    basket.price.toLong().toSumFormat
                )
                tvCashPrice.text = itemView.context?.getString(R.string.sum_text, basket.cash.toLong().toSumFormat)
                tvCardPrice.text = itemView.context?.getString(R.string.sum_text, basket.card.toLong().toSumFormat)
                if (basket.debt > 0) {
                    dot.setImageResource(R.drawable.red_eclipse)
                    tvDebtPrice.text = itemView.context?.getString(
                        R.string.sum_text,
                        "-${(basket.debt.toLong()).toSumFormat}"
                    )
                } else {
                    dot.setImageResource(R.drawable.green_eclipse)
                    tvDebtPrice.text = itemView.context?.getString(
                        R.string.sum_text,
                        (basket.debt.toLong()).toSumFormat
                    )
//                    tvDebtPrice.text = ""
                }
                tvSellerName.text = sales.vendorName
                val createdDate = basket.createdAt.substring(0..9).changeDateFormat
                val createdTime = basket.createdAt.substring(11..18)
                tvDate.text = "$createdDate $createdTime"
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
