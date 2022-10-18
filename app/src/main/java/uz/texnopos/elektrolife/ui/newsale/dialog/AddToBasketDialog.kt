package uz.texnopos.elektrolife.ui.newsale.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
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
import site.texnopos.djakonystar.suminputmask.SumInputMask

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
            val unitId = product.warehouse?.unit?.id ?: -1
            val remained = getString(
                R.string.price_text,
                (if (unitId == 1) r.toLong() else r).toString().sumFormat,
                Constants.getUnitName(requireContext(), unitId)
            )

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


            val type = if (product.warehouse?.unit?.id != 1) SumInputMask.NUMBER_DECIMAL
            else SumInputMask.NUMBER

            etQuantity.setText(if (type == SumInputMask.NUMBER_DECIMAL) "1.0" else "1")

            SumInputMask(etQuantity, type = type)
            SumInputMask(etSumma)

            etQuantity.addTextChangedListener {
                tilQuantity.isErrorEnabled = false
                val count = it.toString().toDouble
                if (count > r || count <= 0.0) {
                    tilQuantity.error = context?.getString(R.string.not_enough_error)
                }
            }

            etSumma.addTextChangedListener {
                sumLiveData.postValue(it.toString().filter { s -> s.isDigit() || s == '.' })
            }

            btnCountMagnet.onClick {
                etQuantity.setText(remained.filter { c -> c.isDigit() || c == '.' })
                etQuantity.setSelection(etQuantity.length())
            }

            btnSummaMagnet.onClick {
                etSumma.setText(product.maxPrice.price.toSumFormat)
                etSumma.setSelection(etSumma.length())
            }

            btnAdd.onClick {
                val quantity = etQuantity.text.toString().toDouble
                val sum = etSumma.text.toString().toDouble

                if (quantity != 0.0 && !tilQuantity.isErrorEnabled && sum != 0.0 && !tilSumma.isErrorEnabled) {
                    onItemAdded.invoke(quantity, sum)
                    dismiss()
                } else {
                    if (quantity == 0.0) {
                        tilQuantity.error = getString(R.string.required_field)
                    }
                    if (sum == 0.0) {
                        tilSumma.error = getString(R.string.required_field)
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
            val sum = s.toDouble
            binding.apply {
                if (product.wholesalePrice.code == "USD") {
                    if (sum < product.wholesalePrice.price * settings.usdToUzs || sum > product.maxPrice.price) {
                        if (!tilSumma.isErrorEnabled) {
                            tilSumma.error = context?.getString(R.string.err_valid_sum)
                        }
                    } else {
                        tilSumma.isErrorEnabled = false
                    }
                } else {
                    if (sum < product.wholesalePrice.price || sum > product.maxPrice.price) {
                        if (!tilSumma.isErrorEnabled) {
                            tilSumma.error = context?.getString(R.string.err_valid_sum)
                        }
                    } else {
                        tilSumma.isErrorEnabled = false
                    }
                }
            }
        }
    }

    private var onItemAdded: (quantity: Double, salePrice: Double) -> Unit = { _, _ -> }
    fun setOnItemAddedListener(onItemClick: (quantity: Double, salePrice: Double) -> Unit) {
        this.onItemAdded = onItemClick
    }

    private var onDismiss: () -> Unit = {}
    fun setOnDismissListener(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
    }
}
