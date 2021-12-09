package uz.texnopos.electrolightwarehouse.ui.client

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.BaseAdapter
import uz.texnopos.electrolightwarehouse.core.extensions.inflate
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.toSumFormat
import uz.texnopos.electrolightwarehouse.data.model.Client
import uz.texnopos.electrolightwarehouse.databinding.ItemClientBinding

class ClientsAdapter : BaseAdapter<Client, ClientsAdapter.ClientsViewHolder>() {
    inner class ClientsViewHolder(private val binding: ItemClientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(model: Client) {
            binding.apply {
                tvName.text = model.name
                tvBalance.text = if (model.balance < 0) {
                    "-${(-1 * model.balance).toString().toSumFormat}"
                } else {
                    model.balance.toString().toSumFormat
                }
                tvUserType.text = if (model.type == 0) "J" else "Yu"
                itemView.onClick {
                    onItemClick.invoke(model)
                }
                ivPhone.onClick {
                    onPhoneClick.invoke(model.phone)
                }
                ivPayment.onClick {
                    onPaymentClick.invoke(model)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientsViewHolder {
        val itemView = parent.inflate(R.layout.item_client)
        val binding = ItemClientBinding.bind(itemView)
        return ClientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientsViewHolder, position: Int) {
        holder.populateModel(models[position])
    }

    private var onItemClick: (model: Client) -> Unit = {}
    fun setOnItemClickListener(onItemClick: (model: Client) -> Unit) {
        this.onItemClick = onItemClick
    }

    private var onPhoneClick: (phone: String) -> Unit = {}
    fun setOnPhoneClickListener(onPhoneClick: (phone: String) -> Unit) {
        this.onPhoneClick = onPhoneClick
    }

    private var onPaymentClick: (model: Client) -> Unit = {}
    fun setOnPaymentClickListener(onPaymentClick: (model: Client) -> Unit) {
        this.onPaymentClick = onPaymentClick
    }
}
