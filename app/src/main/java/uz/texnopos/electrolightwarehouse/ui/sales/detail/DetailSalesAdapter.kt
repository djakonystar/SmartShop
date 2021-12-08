package uz.texnopos.electrolightwarehouse.ui.sales.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.databinding.ItemDetailSalesBinding

class DetailSalesAdapter:RecyclerView.Adapter<DetailSalesAdapter.DetailViewHolder>() {

    var models:List<Sales> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class DetailViewHolder(private val binding:ItemDetailSalesBinding):RecyclerView.ViewHolder(binding.root){
        fun populateModel(sales: Sales){
            binding.apply {
                tvProductName.text = sales.orders[0].product_name
                tvCount.text = sales.orders[0].count.toString()
                tvCost.text = sales.basket[0].cash.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val itemView = parent.inflate(R.layout.item_detail_sales)
        val binding = ItemDetailSalesBinding.bind(itemView)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.populateModel(models[position])
       }

    override fun getItemCount(): Int = models.size
}