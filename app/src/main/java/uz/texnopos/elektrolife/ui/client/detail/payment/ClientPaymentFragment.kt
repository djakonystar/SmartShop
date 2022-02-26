package uz.texnopos.elektrolife.ui.client.detail.payment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.databinding.FragmentClientPaymentBinding

class ClientPaymentFragment(private val client: Client) :
    Fragment(R.layout.fragment_client_payment) {
    private lateinit var binding: FragmentClientPaymentBinding
    private val adapter: ClientPaymentAdapter by inject()
    private val viewModel: ClientPaymentViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentClientPaymentBinding.bind(view)

        binding.apply {
            recyclerView.adapter = adapter

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                viewModel.getClientPayments(clientId = client.id)
            }
        }

        viewModel.getClientPayments(clientId = client.id)
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.clientPayments.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        adapter.models = it.data.payload
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }
    }
}
