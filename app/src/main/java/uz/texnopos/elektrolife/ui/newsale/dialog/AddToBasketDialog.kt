package uz.texnopos.elektrolife.ui.newsale.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.DialogAddToBasketBinding
import uz.texnopos.elektrolife.settings.Settings

class AddToBasketDialog(private val product: Product) : DialogFragment() {
    private lateinit var binding: DialogAddToBasketBinding
    private val settings: Settings by inject()
    private var visibilityLiveData = MutableLiveData<Boolean>()
    private var sumLiveData = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_add_to_basket, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogAddToBasketBinding.bind(view)

        setUpObservers()

        binding.apply {
            val r = product.warehouse?.count ?: 0.0
            val remained = if (product.warehouse?.unit?.id == 1) r.toInt() else r
            var isVisible = false
            visibilityLiveData.postValue(isVisible)

            tvProductName.text = product.name
            tvWholesale.text = getString(
                R.string.wholesale_price_text,
                product.wholesalePrice.price.toSumFormat,
                product.wholesalePrice.code
            )
            tvMin.text =
                context?.getString(
                    R.string.min_price_text,
                    product.minPrice.price.toSumFormat,
                    product.minPrice.code
                )
            tvMax.text =
                context?.getString(
                    R.string.max_price_text,
                    product.maxPrice.price.toSumFormat,
                    product.maxPrice.code
                )

            tilQuantity.suffixText = "/$remained"
            tilSumma.suffixText = settings.currency

            etSumma.setText(product.maxPrice.price.toSumFormat)

            Log.d("unitId", "${product.warehouse?.unit?.id}")
            if (product.warehouse?.unit?.id == 1) etQuantity.setBlockFilter("-.,")
            else etQuantity.filterForDouble

            etSumma.filterForDouble

            etQuantity.addTextChangedListener {
                val count = it.toString().toDouble
                val c = if (product.warehouse?.unit?.id == 1) count.toInt() else count
                tilQuantity.isErrorEnabled = false
                if (count > remained.toDouble() || count <= 0.0) {
                    tilQuantity.error = context?.getString(R.string.not_enough_error)
                }
            }

            etSumma.addTextChangedListener {
                sumLiveData.postValue(it.toString().filter { s -> s.isDigit() || s == '.' })
            }

            btnAdd.onClick {
                val quantity = etQuantity.text.toString().toDouble
                var sum = etSumma.text.toString()

                if (quantity != 0.0 && !tilQuantity.isErrorEnabled && sum.isNotEmpty() && !tilSumma.isErrorEnabled) {
                    sum = sum.filter { s -> s.isDigit() || s == '.' }
                    onItemAdded.invoke(quantity, sum)
                    dismiss()
                } else {
                    if (quantity == 0.0) {
                        tilQuantity.error = getString(R.string.required_field)
                    }
                    if (sum.isEmpty()) {
                        tilSumma.error = context?.getString(R.string.required_field)
                    }
                }
            }

            ivVisible.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    visibilityLiveData.postValue(false)
                } else {
                    visibilityLiveData.postValue(true)
                }
                return@setOnTouchListener true
            }

            ivVisible.onClick {
                isVisible = !isVisible
                visibilityLiveData.postValue(isVisible)
            }

            btnCancel.onClick {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss.invoke()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObservers() {
        visibilityLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                tvWholesale.isVisible = it
                tvMin.isVisible = it
            }
        }

        sumLiveData.observe(viewLifecycleOwner) { s ->
            val sum = s.ifEmpty { "0" }.toDouble()
            binding.apply {
                if (sum < product.wholesalePrice.price * settings.usdToUzs || sum > product.maxPrice.price) {
                    if (!tilSumma.isErrorEnabled) {
                        tilSumma.error = context?.getString(R.string.err_valid_sum)
                    }
                } else {
                    tilSumma.isErrorEnabled = false
                }
            }
        }
    }

    private var onItemAdded: (quantity: Double, salePrice: String) -> Unit = { _, _ -> }
    fun setOnItemAddedListener(onItemClick: (quantity: Double, salePrice: String) -> Unit) {
        this.onItemAdded = onItemClick
    }

    private var onDismiss: () -> Unit = {}
    fun setOnDismissListener(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }
}
