package uz.texnopos.elektrolife.ui.newpayment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcherPayment
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.data.model.newpayment.NewPayment
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentPaymentNewBinding

class NewPaymentFragment : Fragment(R.layout.fragment_payment_new) {
    private lateinit var binding: FragmentPaymentNewBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private lateinit var client: Client
    private val viewModel: NewPaymentViewModel by viewModel()
    private val args: NewPaymentFragmentArgs by navArgs()
    private var clientId: Int = 0
    private var searchValue: String = ""
    private var clientName = ""
    private var clientNameLiveData = MutableLiveData<String>()
    private var list: MutableSet<String> = mutableSetOf()
    private var listClients: MutableMap<String, Client> = mutableMapOf()
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentNewBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()
        client = Client(0, "", "", "", 0, "", 0)

        setUpObservers()

        val fromClientFragment = args.client != "null"

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_payment)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            if (fromClientFragment) {
                client = Gson().fromJson(args.client, Client::class.java)
                tilClient.isEnabled = false
                list.add("${client.name}, ${client.phone}")
                if (!listClients.contains("${client.name}, ${client.phone}"))
                    listClients["${client.name}, ${client.phone}"] = client
                etSearchClient.setText("${client.name}, ${client.phone}")
                clientNameLiveData.postValue(etSearchClient.text.toString())
                clientId = client.id
            } else {
                viewModel.searchClient("")
            }

            etCard.addTextChangedListener(MaskWatcherPayment(etCard))
            etCash.addTextChangedListener(MaskWatcherPayment(etCash))
            tvBalance.text = context?.getString(R.string.sum_text, "0")

            etSearchClient.onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    tilClient.isErrorEnabled = false
                    clientName = parent.getItemAtPosition(position).toString()
                    client = listClients.getValue(clientName)
                    clientId = client.id
                }
            etSearchClient.addTextChangedListener {
                clientNameLiveData.postValue(it.toString())
                list.clear()
                searchValue = it.toString()
                viewModel.searchClient(searchValue)
            }
            btnCashMagnet.onClick {
                client.balance?.let { balance ->
                    if (balance < 0) {
                        etCard.text?.clear()
                        etCash.setText((-balance).toString())
                    }
                }
            }
            btnCardMagnet.onClick {
                client.balance?.let { balance ->
                    if (balance < 0) {
                        etCash.text?.clear()
                        etCard.setText((-balance).toString())
                    }
                }
            }

            btnAddPayment.onClick {
                checkAndSend()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            scrollView.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.newPayment.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        showSuccess(getString(R.string.payment_successfully))
                            .setOnPositiveButtonClickListener {
                                navController.popBackStack()
                            }
                        binding.apply {
                            etSearchClient.text.clear()
                            tilClient.isEnabled = true
                            etCard.text!!.clear()
                            etCash.text!!.clear()
                            etComment.text!!.clear()
                        }
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

        viewModel.searchClient.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        it.data.payload.forEach { client1 ->
                            list.add("${client1.name}, ${client1.phone}")
                            if (!listClients.contains("${client1.name}, ${client1.phone}"))
                                listClients["${client1.name}, ${client1.phone}"] = client1
                            adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.item_spinner,
                                list.toMutableList()
                            )
                            binding.etSearchClient.setAdapter(adapter)
                            binding.etSearchClient.showDropDown()
                        }
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

        clientNameLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                if (listClients.containsKey(it)) {
                    val currentClient = listClients.getValue(it)
                    tvBalance.text =
                        context?.getString(R.string.sum_text, currentClient.balance?.toSumFormat)
                    currentClient.balance?.let { balance ->
                        tvBalance.setTextColor(
                            when {
                                balance < 0 -> {
                                    ContextCompat.getColor(requireContext(), R.color.error_color)
                                }
                                balance > 0 -> {
                                    ContextCompat.getColor(requireContext(), R.color.app_main_color)
                                }
                                else -> {
                                    ContextCompat.getColor(requireContext(), R.color.black)
                                }
                            }
                        )
                    }
                } else {
                    tvBalance.text = context?.getString(R.string.sum_text, "0")
                    tvBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }
    }

    private fun checkAndSend() {
        binding.apply {
            val selectedClient = etSearchClient.text.toString()
            if (!listClients.containsKey(selectedClient)) clientId = -1
            val cash = etCash.text.toString().getOnlyDigits().toLong()
            val card = etCard.text.toString().getOnlyDigits().toLong()
            val comment = etComment.text.toString()

            if (clientId == -1) {
                tilClient.error = context?.getString(R.string.required_field)
                etSearchClient.showDropDown()
            } else {
                viewModel.newPayment(
                    NewPayment(
                        clientId = clientId,
                        cash = cash,
                        card = card,
                        description = comment
                    )
                )
            }
        }
    }
}
