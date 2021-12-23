package uz.texnopos.elektrolife.ui.newsale.dialog

import android.annotation.SuppressLint
import androidx.core.widget.addTextChangedListener
import uz.texnopos.elektrolife.core.extensions.onClick
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.MaskWatcherNothing
import uz.texnopos.elektrolife.core.MaskWatcherPrice
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.DialogAddToBasketBinding

class AddToBasketDialog(private val product: Product) : DialogFragment() {
    private lateinit var binding: DialogAddToBasketBinding
    private var liveQuantity = MutableLiveData<Long>()
    private var visibilityLiveData = MutableLiveData<Boolean>()

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

        observe()

        binding.apply {
            val remained = product.remained
            var isVisible = false
            visibilityLiveData.postValue(isVisible)

            tvWholesale.text = context?.getString(
                R.string.wholesale_price_text,
                product.priceWholesale.toLong().toSumFormat
            )
            tvMin.text =
                context?.getString(R.string.min_price_text, product.priceMin.toLong().toSumFormat)
            tvMax.text =
                context?.getString(R.string.max_price_text, product.priceMax.toLong().toSumFormat)
            tvQuantityCounter.text =
                context?.getString(R.string.counter_text, "0", remained.toSumFormat)
            etSumma.setText(product.priceMax.toLong().toSumFormat)

            etQuantity.addTextChangedListener(MaskWatcherNothing(etQuantity))
            etSumma.addTextChangedListener(MaskWatcherPrice(etSumma))

            etQuantity.addTextChangedListener {
                val count = it.toString().getOnlyDigits()
                tilQuantity.isErrorEnabled = false
                tvQuantityCounter.text =
                    context?.getString(
                        R.string.counter_text,
                        count.toSumFormat,
                        remained.toSumFormat
                    )
                if (count.toInt() > remained) {
                    tilQuantity.error = context?.getString(R.string.not_enough_error)
                }
            }

            etSumma.addTextChangedListener {
                tilSumma.isErrorEnabled = false
            }

            btnAdd.onClick {
                val quantity = etQuantity.text.toString()
                var sum = etSumma.text.toString()
                if (quantity.isNotEmpty() && !tilQuantity.isErrorEnabled && sum.isNotEmpty()) {
                    val quantityInt = quantity.getOnlyDigits().toInt()
                    sum = sum.getOnlyDigits()
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
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    visibilityLiveData.postValue(true)
                } else {
                    visibilityLiveData.postValue(false)
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

    private fun String.getOnlyDigits(): String {
        val s = this.filter { it.isDigit() }
        return if (s.isEmpty()) "0" else s
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        visibilityLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                tvWholesale.isVisible = it
                tvMin.isVisible = it
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
