package uz.texnopos.electrolightwarehouse.ui.client

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.dialPhoneFull
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.showMessage
import uz.texnopos.electrolightwarehouse.data.model.Client
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentClientsBinding

class ClientsFragment : Fragment(R.layout.fragment_clients) {
    private lateinit var binding: FragmentClientsBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: ClientsViewModel by viewModel()
    private val adapter: ClientsAdapter by inject()
    private var clientsList = listOf<Client>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentClientsBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.clients)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                viewModel.getClients()
            }

            recyclerView.adapter = adapter

            etSearch.addTextChangedListener { text ->
                if (text.toString().isEmpty()) {
                    adapter.models = clientsList
                } else {
                    adapter.models = clientsList.filter {
                        it.name.contains(text.toString(), true)
                                || it.phone.contains(text.toString())
                    }
                }
            }

            adapter.setOnItemClickListener { client ->
                val clientDetailDialogFragment = ClientDetailDialogFragment(client)
                clientDetailDialogFragment.show(
                    requireActivity().supportFragmentManager,
                    "ClientDetail"
                )
            }

            adapter.setOnPhoneClickListener { phone ->
                phone.dialPhoneFull(requireActivity())
            }

            adapter.setOnPaymentClickListener { client ->
                val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                val jsonString = gsonPretty.toJson(
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
                    ClientsFragmentDirections.actionClientsFragmentToNewPayment(client = jsonString)
                )
            }
        }

        viewModel.getClients()
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.clients.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        adapter.models =
                            if (it.data.payload.isEmpty()) listOf() else it.data.payload
                        clientsList = if (it.data.payload.isEmpty()) listOf() else it.data.payload
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
