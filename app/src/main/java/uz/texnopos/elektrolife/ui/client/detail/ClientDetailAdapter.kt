package uz.texnopos.elektrolife.ui.client.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.ui.client.detail.payment.ClientPaymentFragment
import uz.texnopos.elektrolife.ui.client.detail.sale.ClientSalesFragment

class ClientDetailAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val client: Client
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ClientSalesFragment(client)
        else -> ClientPaymentFragment(client)
    }
}
