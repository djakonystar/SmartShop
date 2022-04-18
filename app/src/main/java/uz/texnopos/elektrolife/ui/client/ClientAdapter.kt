package uz.texnopos.elektrolife.ui.client

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.BaseAdapter
import uz.texnopos.elektrolife.core.extensions.inflate
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.databinding.ItemClientBinding

class ClientAdapter : BaseAdapter<Client, ClientAdapter.ClientsViewHolder>() {
    inner class ClientsViewHolder(private val binding: ItemClientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun populateModel(model: Client) {
            binding.apply {
                tvName.text = model.name
                tvBalance.text = if (model.balance ?: 0 < 0) {
                    tvBalance.setTextColor(itemView.resources.getColor(R.color.error_color))
                    tvSum.setTextColor(itemView.resources.getColor(R.color.error_color))
                    "-${(-1 * model.balance!!).toString().toSumFormat}"
                } else {
                    tvBalance.setTextColor(itemView.resources.getColor(R.color.app_main_color))
                    tvSum.setTextColor(itemView.resources.getColor(R.color.app_main_color))
                    model.balance.toString().toSumFormat
                }
                tvUserType.text =
                    if (model.type == "J") itemView.context?.getString(R.string.natural_person_short) else itemView.context?.getString(
                        R.string.legal_person_short
                    )
                itemView.onClick {
                    onItemClick.invoke(model)
                }
                ivPhone.onClick {
                    onPhoneClick.invoke(model.phone)
                }
                ivPayment.onClick {
                    onPaymentClick.invoke(model)
                }
                ivInfo.onClick {
                    onInfoClick.invoke(model)
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

    private var onInfoClick: (model: Client) -> Unit = {}
    fun setOnInfoClickListener(onInfoClick: (model: Client) -> Unit) {
        this.onInfoClick = onInfoClick
    }
}
