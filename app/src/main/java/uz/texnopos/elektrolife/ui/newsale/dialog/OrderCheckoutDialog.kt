package uz.texnopos.elektrolife.ui.newsale.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.newclient.Client
import uz.texnopos.elektrolife.databinding.DialogCheckoutOrderBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.newclient.NewClientViewModel
import uz.texnopos.elektrolife.ui.newpayment.NewPaymentViewModel
import site.texnopos.djakonystar.suminputmask.SumInputMask
import java.text.SimpleDateFormat
import java.util.*

class OrderCheckoutDialog(private val totalPrice: Double) : DialogFragment() {
    private lateinit var binding: DialogCheckoutOrderBinding
    private lateinit var addClientDialog: AddClientDialog
    private val newClientsViewModel: NewClientViewModel by viewModel()
    private val newPaymentViewModel: NewPaymentViewModel by viewModel()
    private val settings: Settings by inject()
    private var clientsList: MutableSet<String> = mutableSetOf()
    private var clientsIds: MutableMap<String, Int> = mutableMapOf()
    private var clientName = ""
    private var clientId = 1
    private var newClient = ""
    private var date = ""
    private var dateForBackend = ""
    private var dateInLong = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_checkout_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogCheckoutOrderBinding.bind(view)

        binding.apply {
            tvTitle.text = context?.getString(R.string.sum_text, totalPrice.toSumFormat)
            calculateDebt()

            etSearchClient.addTextChangedListener {
                clientsList.clear()
                newPaymentViewModel.searchClient(it.toString())
            }
            etSearchClient.setOnItemClickListener { adapterView, _, i, _ ->
                clientName = adapterView.getItemAtPosition(i).toString()
                clientId = clientsIds.getValue(clientName)
            }
            btnAddClient.onClick {
                addClientDialog = AddClientDialog()
                addClientDialog.show(requireActivity().supportFragmentManager, addClientDialog.tag)
                addClientDialog.setData { name, inn, phone, type, comment ->
                    newClient = "$name, $phone"
                    newClientsViewModel.registerNewClient(
                        Client(
                            name = name,
                            phone = phone,
                            inn = inn,
                            about = comment,
                            clientType = if (type == 1) "Y" else "J"
                        )
                    )
                }
            }
            tilCash.suffixText = settings.currency
            tilCard.suffixText = settings.currency

            SumInputMask(etCash, etCard)

            etCash.addTextChangedListener {
                tilCash.isErrorEnabled = false
                tilCard.isErrorEnabled = false
                calculateDebt()
            }
            btnCashMagnet.onClick {
                etCard.text?.clear()
                etCash.setText(totalPrice.format(2).sumFormat)
                etCash.setSelection(etCash.length())
            }
            etCard.addTextChangedListener {
                tilCash.isErrorEnabled = false
                tilCard.isErrorEnabled = false
                calculateDebt()
            }
            btnCardMagnet.onClick {
                etCash.text?.clear()
                etCard.setText(totalPrice.format(2).sumFormat)
                etCard.setSelection(etCard.length())
            }
            etDate.addTextChangedListener {
                tilDate.isErrorEnabled = false
            }

            etDate.onClick {
                tilDate.isEnabled = false
                val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(context?.getString(R.string.choose_date_uz))
                    .setSelection(dateInLong)
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setStart(System.currentTimeMillis())
                            .setValidator(DateValidatorPointForward.now())
                            .build()
                    )
                    .build()

                datePickerDialog.addOnPositiveButtonClickListener {
                    dateInLong = it
                    date = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateInLong)
                    dateForBackend = date.changeDateFormat
                    etDate.setText(date)
                }

                datePickerDialog.addOnDismissListener {
                    tilDate.isEnabled = true
                }

                datePickerDialog.show(
                    requireActivity().supportFragmentManager,
                    datePickerDialog.tag
                )
            }

            btnAdd.onClick {
                checkAndSend()
            }

            btnCancel.onClick {
                dismiss()
            }
        }

        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            mainContainer.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        newPaymentViewModel.clients.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data!!.data.clients.forEach { client ->
                        clientsList.clear()
                        clientsList.add("${client.name}, ${client.phone}")
                        if (!clientsIds.contains("${client.name}, ${client.phone}"))
                            clientsIds["${client.name}, ${client.phone}"] = client.id
                        val arrayAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.item_spinner,
                            clientsList.toMutableList()
                        )
                        binding.apply {
                            etSearchClient.setAdapter(arrayAdapter)
                            etSearchClient.showDropDown()
                        }
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        newClientsViewModel.registerNewClient.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    binding.etSearchClient.setText(newClient)
                    clientId = it.data!!.id
                    showSuccess(getString(R.string.client_successfully_added))
                        .setOnPositiveButtonClickListener {
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

    private fun calculateDebt() {
        binding.apply {
            val cashPrice = etCash.text.toString().toDouble
            val cardPrice = etCard.text.toString().toDouble
            val remind = totalPrice - cashPrice - cardPrice
            if (remind > 0) {
                tvDebtPrice.text = context?.getString(R.string.sum_text, "-${remind.toSumFormat}")
            } else {
                tvDebtPrice.text = context?.getString(R.string.sum_text, (-remind).toSumFormat)
            }

            btnAdd.isEnabled = remind in -500.0..0.0 || remind > 0

            tvDebtPrice.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    when {
                        remind > 0 -> R.color.error_color
                        remind == 0.0 -> R.color.black
                        else -> R.color.app_main_color
                    }
                )
            )
        }
    }

    private fun checkAndSend() {
        binding.apply {
            val cash = etCash.text.toString().toDouble
            val card = etCard.text.toString().toDouble
            val debt = if (cash + card < totalPrice) totalPrice - (cash + card) else 0.0
            var dateRequired = false
            val currentClientName = etSearchClient.text.toString()
            clientId = if (clientsList.contains(currentClientName)) {
                clientsIds.getValue(clientName)
            } else 1
            val comment = etComment.text.toString()

            if (debt > 0) {
                dateRequired = true
                if (clientId == 1) clientId = -1
            }
            if (dateRequired) {
                if (date.isEmpty() || clientId == -1) {
                    if (date.isEmpty()) {
                        tilDate.error = context?.getString(R.string.required_field)
                    }
                    if (clientId == -1) {
                        tilClient.error = context?.getString(R.string.required_field)
                    }
                } else {
                    sendDate.invoke(clientId, cash, card, debt, dateForBackend, comment)
                }
            } else {
                sendDate.invoke(clientId, cash, card, debt, dateForBackend, comment)
            }
        }
    }

    private var sendDate: (clientId: Int, cash: Double, card: Double, debt: Double, date: String, comment: String) -> Unit =
        { _: Int, _: Double, _: Double, _: Double, _: String, _: String -> }

    fun sendData(sendDate: (clientId: Int, cash: Double, card: Double, debt: Double, date: String, comment: String) -> Unit) {
        this.sendDate = sendDate
    }
}
