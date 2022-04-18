package uz.texnopos.elektrolife.ui.newsale.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
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
    private var sumLiveCheck = MutableLiveData<String>()
    private var quantityLiveCheck = MutableLiveData<Pair<String, Boolean>>()

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
            val r = product.warehouse.count
            val remained = if (r % 1 == 0.0) r.toInt() else r
            var isVisible = false
            visibilityLiveData.postValue(isVisible)


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
//            tvQuantityCounter.isVisible = false
//            tvQuantityCounter.text =
//                context?.getString(R.string.counter_text, "0", remained.toSumFormat)
            tilSumma.suffixText = settings.currency
            etSumma.setText(product.maxPrice.price.toSumFormat)

            if (remained is Int) {
                etQuantity.setBlockFilter("-,.")
            } else {
                etQuantity.setDoubleFilter
            }
            etSumma.setBlockFilter("-,")

            etQuantity.addTextChangedListener {
                quantityLiveCheck.postValue(Pair(it.toString(), remained is Int))
                val count =
                    it.toString().ifEmpty { "0" }.filter { s -> s.isDigit() || s == '.' }.toDouble()
                tilQuantity.isErrorEnabled = false
//                tvQuantityCounter.text =
                context?.getString(
                    R.string.counter_text,
                    count.toSumFormat,
                    remained.toSumFormat
                )
                if (count > remained.toDouble() || count <= 0.0) {
                    tilQuantity.error = context?.getString(R.string.not_enough_error)
                }
            }

            etSumma.addTextChangedListener {
                sumLiveData.postValue(it.toString().filter { s -> s.isDigit() || s == '.' })
                sumLiveCheck.postValue(it.toString())
            }

            btnAdd.onClick {
                val quantity = etQuantity.text.toString()
                var sum = etSumma.text.toString()
                if (quantity.isNotEmpty() && !tilQuantity.isErrorEnabled && sum.isNotEmpty() && !tilSumma.isErrorEnabled) {
                    val quantityInt = quantity.getOnlyDigits().toInt()
                    sum = sum.filter { s -> s.isDigit() || s == '.' }
                    onItemClick.invoke(quantityInt, sum)
                    dismiss()
                } else {
                    if (quantity.isEmpty()) {
                        tilQuantity.error = context?.getString(R.string.required_field)
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

        sumLiveCheck.observe(viewLifecycleOwner) { sumText ->
            binding.apply {
                if (!sumText.contains('.')) etSumma.setBlockFilter("-,")
                else etSumma.setDoubleFilter
            }
        }

        quantityLiveCheck.observe(viewLifecycleOwner) { quantity ->
            binding.apply {
                if (!quantity.first.contains('.') && !quantity.second) etQuantity.setBlockFilter("-,")
                else etQuantity.setDoubleFilter
            }
        }
    }

    private var onItemClick: (quantity: Int, summa: String) -> Unit = { _, _ -> }
    fun onItemClickListener(onItemClick: (quantity: Int, summa: String) -> Unit) {
        this.onItemClick = onItemClick
    }

    private var onDismiss: () -> Unit = {}
    fun onDismissListener(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }
}
