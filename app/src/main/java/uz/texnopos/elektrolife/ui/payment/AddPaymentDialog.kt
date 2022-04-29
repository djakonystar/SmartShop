package uz.texnopos.elektrolife.ui.payment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.payment.AddPayment
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.data.model.sales.OrderResponse
import uz.texnopos.elektrolife.databinding.DialogAddPaymentBinding
import uz.texnopos.elektrolife.settings.Settings

class AddPaymentDialog(private val order: OrderResponse) : DialogFragment() {
    private lateinit var binding: DialogAddPaymentBinding
    private val viewModel: PaymentViewModel by viewModel()
    private val settings: Settings by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_add_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogAddPaymentBinding.bind(view)

        binding.apply {
            tvDebtPrice.text = getString(
                R.string.debt_remained_price_text,
                order.amount.remaining.toSumFormat,
                settings.currency
            )

            etCash.filterForDouble
            etCard.filterForDouble
            tilCash.suffixText = settings.currency
            tilCard.suffixText = settings.currency

            etCash.addTextChangedListener {
                tilCash.isErrorEnabled = false
                tilCard.isErrorEnabled = false
            }

            btnCashMagnet.onClick {
                etCard.text?.clear()
                etCash.setText(order.amount.remaining.toString())
            }

            etCard.addTextChangedListener {
                tilCash.isErrorEnabled = false
                tilCard.isErrorEnabled = false
            }

            btnCardMagnet.onClick {
                etCash.text?.clear()
                etCard.setText(order.amount.remaining.toString())
            }

            btnPay.onClick {
                val cash = etCash.text.toString().toDouble
                val card = etCard.text.toString().toDouble

                if (cash == 0.0 && card == 0.0) {
                    tilCash.error = getString(R.string.fill_the_fields)
                    tilCard.error = getString(R.string.fill_the_fields)
                } else {
                    val payment = AddPayment(basketId = order.id, cash = cash, card = card)
                    viewModel.addPayment(payment)
                }
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
        viewModel.addPayment.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(getString(R.string.payment_successfully))
                        .setOnDismissListener {
                            dismiss()
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

    override fun onDismiss(dialog: DialogInterface) {
        onDismiss()
        super.onDismiss(dialog)
    }
}
