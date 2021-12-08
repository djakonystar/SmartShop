package uz.texnopos.electrolightwarehouse.ui.sales

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.databinding.ItemSalesBinding

class SalesAdapter: RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {

    var models:List<Sales> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val itemView = parent.inflate(R.layout.item_sales)
        val binding = ItemSalesBinding.bind(itemView)
        return SalesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        holder.populateModel(models[position])
    }

    override fun getItemCount(): Int = models.size

    inner class SalesViewHolder(private val binding:ItemSalesBinding):RecyclerView.ViewHolder(binding.root){
        fun populateModel(sales:Sales){
            binding.apply {
                tvClientName.text = sales.client_name
                tvTotalPrice.text = sales.basket[0].price.toString()
                if (sales.basket[0].debt>0){
                    dot.setBackgroundResource(R.drawable.red_eclipse)
                }else{
                    dot.setBackgroundResource(R.drawable.green_eclipse)
                }
                tvData.text = sales.basket[0].created_at
                if (sales.basket[0].cash>0){
                    tvPaymentInfo.text = "Naqt"
                }else{
                    tvPaymentInfo.text = "Karta"
                }
            }
            binding.root.onClick {
                onClickItem.invoke(sales)
            }
        }
    }

    private var onClickItem:(sales:Sales)->Unit={}
    fun onClickItem(clickItem:(sales:Sales)->Unit){
        this.onClickItem=clickItem
    }
}