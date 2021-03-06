package uz.texnopos.elektrolife.ui.client

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
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
import uz.texnopos.elektrolife.data.model.newclient.RegisterClient
import uz.texnopos.elektrolife.databinding.ActionBarClientBinding
import uz.texnopos.elektrolife.databinding.FragmentClientBinding
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
    private var isLoading = false
    private var page = 1
    private var limit = 50
    private var mutableClient: MutableList<Client> = mutableListOf()

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
                        RegisterClient(
                            name = name,
                            phone = phone,
                            inn = inn,
                            about = comment,
                            clientType = type
                        )
                    )
                }
            }
        }

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                mutableClient = mutableListOf()
                page = 1
                viewModel.getClients(limit, page, etSearch.text.toString())
            }
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (!isLoading) {
                            if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                                page++
                                viewModel.getClients(limit, page, etSearch.text.toString())
                            }
                        }
                    }
                }
            )

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    p0?.let {
                        if (it.isEmpty()) {
                            mutableClient = mutableListOf()
                            page = 1
                            viewModel.getClients(limit, page, "")
                        } else {
                            mutableClient = mutableListOf()
                            page = 1
                            viewModel.getClients(limit, page, it.toString())
                        }
                    }
                }

            })

            adapter.setOnItemClickListener { client ->
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

        viewModel.getClients(limit, 1, "")
        setUpObservers()
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
                    it.data.let { data ->
                        if (data!!.successful) {
                            data.payload.forEach { client ->
                                if (!mutableClient.contains(client)) {
                                    mutableClient.add(client)
                                }
                            }
                            adapter.models = mutableClient
                            val debts = mutableClient.filter { c -> c.balance!! < 0 }
                                .sumOf { c -> c.balance!! }.toLong()
                            binding.tvDebtPrice.text =
                                context?.getString(R.string.total_debt_text, debts.toSumFormat)
                            page++
                        } else {
                            showError(it.data!!.message)
                        }
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        newClientViewModel.registerNewClient.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(getString(R.string.client_successfully_added))
                        .setOnDismissListener {
                            mutableClient = mutableListOf()
                            viewModel.getClients(limit, 1, "")
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
