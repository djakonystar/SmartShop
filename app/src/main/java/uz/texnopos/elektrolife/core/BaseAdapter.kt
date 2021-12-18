package uz.texnopos.elektrolife.core

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    var models: List<T> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun onAdded(data: T) {
        val list = models.toMutableList()
        list.add(data)
        models = list
        notifyItemInserted(list.lastIndex)
    }
    fun onRemoved(data: T) {
        val index = models.indexOf(data)
        val list = models.toMutableList()
        list.removeAt(index)
        models = list
        notifyItemRemoved(index)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = models.size
}
