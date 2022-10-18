package uz.texnopos.elektrolife.ui.client.detail.sale

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.data.model.sales.Sales
import uz.texnopos.elektrolife.databinding.FragmentClientSaleBinding
import uz.texnopos.elektrolife.ui.client.detail.ClientDetailFragmentDirections
import uz.texnopos.elektrolife.ui.sales.SalesAdapter

class ClientSalesFragment(private val client: Client) : Fragment(R.layout.fragment_client_sale) {
    private lateinit var binding: FragmentClientSaleBinding
    private lateinit var navController: NavController
    private val adapter: SalesAdapter by inject()
    private val viewModel: ClientSalesViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentClientSaleBinding.bind(view)
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)

        binding.apply {
            recyclerView.adapter = adapter

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                viewModel.getClientSales(clientId = client.id)
            }
        }

        adapter.onClickItem { sales ->
            val saleStr = GsonBuilder().setPrettyPrinting().create().toJson(sales)
            navController.navigate(
                ClientDetailFragmentDirections.actionClientDetailFragmentToDetailSalesFragment(
                    saleStr
                )
            )
        }

        viewModel.getClientSales(clientId = client.id)
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.clientSales.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        adapter.models = it.data.payload
                    } else {
                        showError(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
