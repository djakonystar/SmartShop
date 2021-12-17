package uz.texnopos.electrolightwarehouse.ui.newsale

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.CatalogCategory
import uz.texnopos.electrolightwarehouse.databinding.ItemHorizontalOrdersCategoriesBinding

class CategoryNewSaleAdapter : RecyclerView.Adapter<CategoryNewSaleAdapter.OrderHorizontalViewHolder>() {
    var selectedItem = -1
        set(value) {
            if (selectedItem != -1) {
                models[field].selectedMood = false
                notifyItemChanged(field)
            }
            field = value
            models[field].selectedMood = true
            notifyItemChanged(value)
        }
    var models: List<CatalogCategory> = listOf()
        set(value) {
            field = value
            if (models.isNotEmpty())
            selectedItem = 0
            notifyDataSetChanged()
            if (models.isNotEmpty())
            onItemClick.invoke(models[0].id)
        }

    inner class OrderHorizontalViewHolder(private val binding: ItemHorizontalOrdersCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun populateModel(model: CatalogCategory, position: Int) {
            binding.apply {
                tvCategories.text = model.name
                if (model.selectedMood) {
                    tvCategories.setTextColor(Color.WHITE)
                    tvCategories.textSize = 18F
                    itemView.setBackgroundResource(R.drawable.shape_square_for_horizontal_item_categories)
                    tvCategories.setTypeface(tvCategories.typeface, Typeface.BOLD)
                } else {
                    tvCategories.setTextColor(Color.BLACK)
                    tvCategories.setTypeface(tvCategories.typeface, Typeface.BOLD)
                    tvCategories.paintFlags = 0
                    itemView.setBackgroundResource(R.drawable.shape_line_for_horizontal_item_categories)
                    tvCategories.textSize = 16F
                }
            }
            binding.root.onClick {
                onItemClick.invoke(model.id)
                selectedItem = position
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHorizontalViewHolder {
        val itemView = parent.inflate(R.layout.item_horizontal_orders_categories)
        val binding = ItemHorizontalOrdersCategoriesBinding.bind(itemView)
        return OrderHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHorizontalViewHolder, position: Int) {
        holder.populateModel(models[position], position)
    }

    override fun getItemCount(): Int = models.size

    var onItemClick: (categoryId: Int) -> Unit = {}
    fun onItemClickListener(onItemClick: (categoryId: Int) -> Unit) {
        this.onItemClick = onItemClick
    }
}