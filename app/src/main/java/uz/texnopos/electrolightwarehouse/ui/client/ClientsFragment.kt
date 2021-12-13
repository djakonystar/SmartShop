package uz.texnopos.electrolightwarehouse.ui.client

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
    private var isLoading = false
    private var page = 1
    private var limit = 50
    private var mutableClient: MutableList<Client> = mutableListOf()

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
                viewModel.getClients(limit,1,"")
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
                                viewModel.getClients(limit,page,etSearch.text.toString())
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
                            page = 1
                            viewModel.getClients(limit,page,"")
                        } else {
                            mutableClient = mutableListOf()
                            page = 1
                            viewModel.getClients(limit,page,it.toString())
                        }
                    }
                }

            })

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

        viewModel.getClients(limit,1,"")
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
                    it.data.let { data ->
                        if (data!!.successful) {
                            data.payload.forEach { client ->
                                if (!mutableClient.contains(client)) {
                                    mutableClient.add(client)
                                }
                            }
                            adapter.models = mutableClient
                            page++
                        } else {
                            showMessage(it.data!!.message)
                        }
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
