package uz.texnopos.elektrolife.ui.finance.salary

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.toDouble
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.finance.salary.Salary
import uz.texnopos.elektrolife.databinding.ItemSalaryBinding
import uz.texnopos.elektrolife.settings.Settings

class SalaryAdapter(private val settings: Settings) :
    BaseAdapter<Salary, SalaryAdapter.SalaryViewHolder>() {
    inner class SalaryViewHolder(private val binding: ItemSalaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(salary: Salary) {
            binding.apply {
                tvName.text = salary.employee.name
                tvSalary.text = itemView.context.getString(
                    R.string.price_text,
                    salary.sum.toString().toDouble.toSumFormat,
                    settings.currency
                )

                root.onClick {
                    onItemClick(salary)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryViewHolder {
        val itemView = parent.inflate(R.layout.item_salary)
        val binding = ItemSalaryBinding.bind(itemView)
        return SalaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalaryViewHolder, position: Int) {
        holder.populateModel(models[position])
    }

    private var onItemClick: (salary: Salary) -> Unit = {}
    fun setOnItemClickListener(onItemCLick: (salary: Salary) -> Unit) {
        this.onItemClick = onItemCLick
    }
}
