package uz.texnopos.elektrolife.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.newproduct.Transaction
import uz.texnopos.elektrolife.data.model.newproduct.TransactionItem
import uz.texnopos.elektrolife.data.model.newproduct.TransactionTransfer
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.DialogTransactionBinding

class TransactionDialog(private val transaction: TransactionTransfer) :
    DialogFragment(R.layout.dialog_transaction) {
    private lateinit var binding: DialogTransactionBinding
    private val transactionViewModel: TransactionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_transaction, container, false)
    }

    var onItemClick: () -> Unit = {}
    fun onItemClickListener(onItemClick: () -> Unit) {
        this.onItemClick = onItemClick
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

            btnAdd.onClick {
                when (val quantity = etProductQuantity.text.toString().toDouble) {
                    0.0 -> tilProductQuantity.error = context?.getString(R.string.required_field)
                    else -> {
                        val postTransaction = Transaction(
                            transactions = listOf(
                                TransactionItem(
                                    productId = transaction.productId,
                                    count = quantity,
                                    unitId = unitId,
                                    price = transaction.price,
                                    maxPrice = transaction.maxPrice,
                                    minPrice = transaction.minPrice,
                                    wholePrice = transaction.wholePrice
                                )
                            )
                        )

                        transactionViewModel.newTransaction(postTransaction)

                        onItemClick.invoke()
                    }
                }
            }

            btnCancel.onClick {
                dismiss()
            }

            setUpObservers()
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            mainContainer.isEnabled = !loading
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
    }

    private var onDismiss: () -> Unit = {}
    fun setOnDismissListener(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }
}
