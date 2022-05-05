package uz.texnopos.elektrolife.ui.client

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.databinding.ActionBarClientBinding
import uz.texnopos.elektrolife.databinding.FragmentClientBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.newclient.NewClientViewModel
import uz.texnopos.elektrolife.ui.newsale.dialog.AddClientDialog

class ClientFragment : Fragment(R.layout.fragment_client) {
    private lateinit var binding: FragmentClientBinding
    private lateinit var abBinding: ActionBarClientBinding
    private lateinit var navController: NavController
    private lateinit var addClientDialog: AddClientDialog
    private val viewModel: ClientViewModel by viewModel()
    private val newClientViewModel: NewClientViewModel by viewModel()
    private val adapter: ClientAdapter by inject()
    private val settings: Settings by inject()
    private var clientsList = mutableListOf<Client>()
    private var searchValue = ""
    private var isLoading = false
    private var page = 1
    private var lastPage = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentClientBinding.bind(view)
        abBinding = ActionBarClientBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.clients)
            btnHome.onClick {
                navController.popBackStack()
            }
            btnAddClient.onClick {
                addClientDialog = AddClientDialog()
                addClientDialog.show(requireActivity().supportFragmentManager, "")
                addClientDialog.setData { name, inn, phone, type, comment ->
                    newClientViewModel.registerNewClient(
                        newClient(
                            name = name,
                            phone = phone,
                            inn = inn,
                            about = comment,
                            clientType = if (type == 1) "Y" else "J"
                        )
                    )
                }
                observeRegisteringClient()
            }
        }

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                clientsList = mutableListOf()
                page = 1
                adapter.models = listOf()
                viewModel.getClients(page, searchValue)
            }
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!isLoading && adapter.models.isNotEmpty() && page < lastPage &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    ) {
                        page++
                        viewModel.getClients(page, searchValue)
                    }
                }
            })

            etSearch.addTextChangedListener {
                searchValue = it.toString()
                searchValue.ifEmpty { page = 1 }
                adapter.models = listOf()
                viewModel.getClients(page, searchValue)
            }

            adapter.setOnItemClickListener { client ->
                val clientStr = GsonBuilder().setPrettyPrinting().create().toJson(
                    client.apply {
                        Client(
                            id = id,
                            name = name,
                            phone = phone,
                            tin = tin,
                            balance = balance,
                            comment = comment,
                            type = type
                        )
                    }
                )
                navController.navigate(
                    ClientFragmentDirections.actionClientsFragmentToClientDetailFragment(clientStr)
                )
            }

            adapter.setOnPhoneClickListener { phone ->
                phone.dialPhone(requireActivity())
            }

            adapter.setOnPaymentClickListener { client ->
                val clientStr = GsonBuilder().setPrettyPrinting().create().toJson(
                    client.apply {
                        Client(
                            id = id,
                            name = name,
                            phone = phone,
                            tin = tin,
                            balance = balance,
                            comment = comment ?: "",
                            type = type
                        )
                    }
                )
                navController.navigate(
                    ClientFragmentDirections.actionClientsFragmentToNewPayment(client = clientStr)
                )
            }

            adapter.setOnInfoClickListener { client ->
                val clientDetailDialogFragment = ClientDetailDialogFragment(client = client)
                clientDetailDialogFragment.show(
                    requireActivity().supportFragmentManager,
                    clientDetailDialogFragment.tag
                )
            }
        }

        viewModel.getClients(page, searchValue)
        setUpObservers()
    }

    override fun onDetach() {
        adapter.models = listOf()
        super.onDetach()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            recyclerView.isEnabled = !loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.clients.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    lastPage = it.data!!.lastPage
                    if (it.data.currentPage == 1) {
                        binding.tvDebtPrice.text = getString(
                            R.string.debt_price_text,
                            (-it.data.data.debt).toSumFormat,
                            settings.currency
                        )
                    }
                    val allClientsList = it.data.data.clients as MutableList<Client>
                    if (adapter.models.isEmpty()) {
                        adapter.models = allClientsList
                        clientsList = allClientsList
                    } else {
                        it.data.data.clients.forEach { client ->
                            if (!clientsList.contains(client)) {
                                clientsList.add(client)
                            }
                        }
                        adapter.models = clientsList
                    }
//                    val debts = clientsList.filter { c -> c.balance!! < 0 }
//                        .sumOf { c -> c.balance!! }.toLong()
//                    binding.tvDebtPrice.text =
//                        context?.getString(
//                            R.string.total_debt_text,
//                            debts.toSumFormat,
//                            settings.currency
//                        )
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    private fun observeRegisteringClient() {
        newClientViewModel.registerNewClient.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(getString(R.string.client_successfully_added))
                        .setOnDismissListener {
                            adapter.models = listOf()
                            clientsList = mutableListOf()
                            binding.etSearch.text!!.clear()
                            page = 1
                            viewModel.getClients(page, searchValue)
                            addClientDialog.dismiss()
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
