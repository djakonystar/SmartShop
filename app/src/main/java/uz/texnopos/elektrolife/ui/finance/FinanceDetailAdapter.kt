package uz.texnopos.elektrolife.ui.finance

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.finance.Finance
import uz.texnopos.elektrolife.databinding.ItemFinanceDetailBinding

class FinanceDetailAdapter : BaseAdapter<Finance, FinanceDetailAdapter.FinanceDetailViewHolder>() {
    inner class FinanceDetailViewHolder(private val binding: ItemFinanceDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(model: Finance) {
            binding.apply {
                tvDescription.text = model.description
                when (model.paymentType) {
                    "cash" -> ivPaymentType.setImageResource(R.drawable.ic_cash)
                    "card" -> ivPaymentType.setImageResource(R.drawable.ic_card)
                    else -> ivPaymentType.setImageResource(R.drawable.ic_total_money)
                }
                tvSum.text = itemView.context?.getString(R.string.sum_text, model.price.toSumFormat)
                tvDate.text = model.date.changeDateFormat
                tvPayee.text = model.payee

                tvCategoryName.text = when (model.category.uz) {
                    itemView.context?.getString(R.string.expense_administrative_back) ->
                        itemView.context?.getString(R.string.expense_administrative_short)
                    itemView.context?.getString(R.string.expense_rent_back) ->
                        itemView.context?.getString(R.string.expense_rent_short)
                    itemView.context?.getString(R.string.expense_salary_back) ->
                        itemView.context?.getString(R.string.expense_salary_short)
                    itemView.context?.getString(R.string.expense_investments_back) ->
                        itemView.context?.getString(R.string.expense_investments_short)
                    itemView.context?.getString(R.string.expense_office_back) ->
                        itemView.context?.getString(R.string.expense_office_short)
                    itemView.context?.getString(R.string.expense_taxes_back) ->
                        itemView.context?.getString(R.string.expense_taxes_short)
                    itemView.context?.getString(R.string.expense_household_back) ->
                        itemView.context?.getString(R.string.expense_household_short)
                    else -> itemView.context?.getString(R.string.expense_any_short)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceDetailViewHolder {
        val itemView = parent.inflate(R.layout.item_finance_detail)
        val binding = ItemFinanceDetailBinding.bind(itemView)
        return FinanceDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FinanceDetailViewHolder, position: Int) {
        holder.populateModel(models[position])
    }
}