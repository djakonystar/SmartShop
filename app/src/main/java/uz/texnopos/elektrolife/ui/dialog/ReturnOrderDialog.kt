package uz.texnopos.elektrolife.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.sales.Order
import uz.texnopos.elektrolife.data.model.sales.OrderResponse
import uz.texnopos.elektrolife.data.model.sales.returnorder.ReturnOrder
import uz.texnopos.elektrolife.databinding.DialogOrderReturnBinding

class ReturnOrderDialog(private val basket: OrderResponse, private val position: Int) :
    DialogFragment() {
    private lateinit var binding: DialogOrderReturnBinding
    private lateinit var returnOrder: ReturnOrder
    private lateinit var order: Order
    private var paymentType = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_order_return, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogOrderReturnBinding.bind(view)
        order = basket.orders[position]

        binding.apply {
            tvProductName.text = order.productName

            chbCash.isVisible = basket.amount.cash > 0
            chbCard.isVisible = basket.amount.card > 0
            chbDebt.isVisible = basket.amount.debt > 0
            chbDebtPaid.isVisible = basket.amount.paidDebt > 0

            chbCash.setOnCheckedChangeListener { _, b ->
                if (b) paymentType.add("cash")
                else paymentType.remove("cash")
                tvPaymentTypeError.isVisible = false
            }

            chbCard.setOnCheckedChangeListener { _, b ->
                if (b) paymentType.add("card")
                else paymentType.remove("card")
                tvPaymentTypeError.isVisible = false
            }

            chbDebt.setOnCheckedChangeListener { _, b ->
                if (b) paymentType.add("debt")
                else paymentType.remove("debt")
                tvPaymentTypeError.isVisible = false
            }

            chbDebtPaid.setOnCheckedChangeListener { _, b ->
                if (b) paymentType.add("paid_debt")
                else paymentType.remove("paid_debt")
                tvPaymentTypeError.isVisible = false
            }

            val orderedCount = order.count
            val unitId = order.unitId
            val suffix = getString(
                R.string.price_text,
                if (unitId == 1) orderedCount.toULong() else orderedCount,
                Constants.getUnitName(requireContext(), unitId)
            )
            tilCount.suffixText = "/$suffix"

            if (unitId == 1) etCount.setBlockFilter("-,.")
            else etCount.filterForDouble

            etCount.addTextChangedListener {
                tilCount.isErrorEnabled = false
                val count = it.toString().toDouble
                if (count > suffix.substringBefore(' ').toDouble() || count <= 0.0) {
                    tilCount.error = context?.getString(R.string.not_enough_error)
                }
            }

            btnAdd.onClick {
                val count = etCount.text.toString().toDouble
                val checked = paymentType.isNotEmpty()
                if (count != 0.0 && count <= order.count && checked) {
                    returnOrder = ReturnOrder(
                        id = basket.id,
                        paymentType = paymentType,
                        orders = listOf(returningOrder(id = order.id, count = count))
                    )

                    onAddClick(returnOrder)
                } else {
                    if (count == 0.0) tilCount.error = getString(R.string.required_field)
                    if (count > order.count) tilCount.error = getString(R.string.not_enough_error)
                    if (!checked) tvPaymentTypeError.isVisible = true
                }
            }

            btnCancel.onClick {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismiss()
        super.onDismiss(dialog)
    }

    private var onAddClick: (returnOrder: ReturnOrder) -> Unit = {}
    fun setOnAddClickListener(onAddClick: (returnOrder: ReturnOrder) -> Unit) {
        this.onAddClick = onAddClick
    }

    private var onDismiss: () -> Unit = {}
    fun setOnDismissListener(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }
}
