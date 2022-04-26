package uz.texnopos.elektrolife.ui.finance.salary.detail

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.Constants
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.finance.salary.SalaryMonthly
import uz.texnopos.elektrolife.databinding.ItemSalaryDetailBinding
import uz.texnopos.elektrolife.settings.Settings

class SalaryDetailAdapter(private val settings: Settings) :
    BaseAdapter<SalaryMonthly, SalaryDetailAdapter.SalaryDetailViewHolder>() {
    inner class SalaryDetailViewHolder(private val binding: ItemSalaryDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun populateModel(salary: SalaryMonthly) {
            binding.apply {
                tvMonth.text =
                    "${Constants.getMonthName(itemView.context, salary.month)} ${salary.year}"
                tvSalary.text = itemView.context.getString(
                    R.string.price_text,
                    salary.sum.toSumFormat,
                    settings.currency
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryDetailViewHolder {
        val itemView = parent.inflate(R.layout.item_salary_detail)
        val binding = ItemSalaryDetailBinding.bind(itemView)
        return SalaryDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalaryDetailViewHolder, position: Int) {
        holder.populateModel(models[position])
    }
}
