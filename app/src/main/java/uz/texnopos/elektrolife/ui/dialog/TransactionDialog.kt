package uz.texnopos.elektrolife.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.newproduct.Price
import uz.texnopos.elektrolife.data.model.newproduct.Transaction
import uz.texnopos.elektrolife.data.model.newproduct.TransactionItem
import uz.texnopos.elektrolife.data.model.newproduct.TransactionTransfer
import uz.texnopos.elektrolife.databinding.DialogTransactionBinding
import uz.texnopos.elektrolife.ui.currency.CurrencyViewModel

class TransactionDialog(private val transaction: TransactionTransfer) :
    DialogFragment(R.layout.dialog_transaction) {
    private lateinit var binding: DialogTransactionBinding
    private val transactionViewModel: TransactionViewModel by viewModel()
    private val currencyViewModel: CurrencyViewModel by viewModel()
    private var mapOfCurrency: MutableMap<Int, String> = mutableMapOf()
    private var currencyCodeToId = mutableMapOf<String, Int>()
    private var selectedCurrencyId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogTransactionBinding.bind(view)

        binding.apply {
            tvProductName.text = transaction.productName

            val unitId = transaction.unitId
            if (unitId == 1) {
                etProductQuantity.setBlockFilter("-,.")
            } else {
                etProductQuantity.filterForDouble
            }

            val unitName = Constants.getUnitName(requireContext(), unitId)
            tilProductQuantity.suffixText = unitName

            etProductQuantity.addTextChangedListener {
                tilProductQuantity.isErrorEnabled = false
            }

            etCostPrice.setText(transaction.price.price.toSumFormat)

            etCostPrice.addTextChangedListener {
                tilCostPrice.isErrorEnabled = false
            }

            selectedCurrencyId = transaction.price.currencyId
            actCostCurrency.threshold = 100
            actCostCurrency.setOnItemClickListener { _, _, i, _ ->
                selectedCurrencyId = i + 1
            }

            btnAdd.onClick {
                val quantity = etProductQuantity.text.toString().toDouble
                val costPrice = etCostPrice.text.toString().toDouble

                if (quantity != 0.0 && costPrice != 0.0) {
                    val postTransaction = Transaction(
                        transactions = listOf(
                            TransactionItem(
                                productId = transaction.productId,
                                count = quantity,
                                unitId = unitId,
                                price = Price(
                                    selectedCurrencyId,
                                    costPrice
                                )
                            )
                        )
                    )

                    transactionViewModel.newTransaction(postTransaction)
                } else {
                    if (quantity == 0.0) {
                        tilProductQuantity.error = context?.getString(R.string.required_field)
                    }
                    if (costPrice == 0.0) {
                        tilCostPrice.error = context?.getString(R.string.required_field)
                    }
                }
            }

            btnCancel.onClick {
                dismiss()
            }

            currencyViewModel.getCurrency()
            setUpObservers()
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            tilProductQuantity.isEnabled = !loading
            tilCostPrice.isEnabled = !loading
            tilCostCurrency.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        transactionViewModel.transaction.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(context?.getString(R.string.transaction_successful))
                    onDismiss.invoke()
                    dismiss()
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        currencyViewModel.currency.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    binding.apply {
                        mapOfCurrency.clear()
                        it.data!!.forEach { currency ->
                            mapOfCurrency[currency.id] = currency.code
                            currencyCodeToId[currency.code] = currency.id
                        }

                        val currencyList = mapOfCurrency.values.toList()
                        val currencyAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.item_spinner,
                            currencyList
                        )

                        actCostCurrency.setAdapter(currencyAdapter)
                        actCostCurrency.setText(mapOfCurrency[transaction.price.currencyId])
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    private var onDismiss: () -> Unit = {}
    fun setOnDismissListener(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }
}
