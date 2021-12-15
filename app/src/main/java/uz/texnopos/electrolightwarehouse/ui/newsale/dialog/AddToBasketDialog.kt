package uz.texnopos.electrolightwarehouse.ui.newsale.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MaskWatcherNothing
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPrice
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.toSumFormat
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.DialogAddToBasketBinding

class AddToBasketDialog(private val product: Product) : DialogFragment() {
    private lateinit var binding: DialogAddToBasketBinding
    private var liveQuantity = MutableLiveData<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
        return inflater.inflate(R.layout.dialog_add_to_basket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogAddToBasketBinding.bind(view)

        observe()

        binding.apply {
            val remained = 100

            tvWholesale.text =
                context?.getString(R.string.wholesale_price_text, "0")
            tvMin.text = context?.getString(R.string.min_price_text, "0")
            tvMax.text = context?.getString(R.string.max_price_text, "0")
            tvQuantityCounter.text =
                context?.getString(R.string.counter_text, "0", remained.toSumFormat)

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

            etQuantity.doOnTextChanged { it, _, _, _ ->
                if (it.isNullOrEmpty()) {
                    liveQuantity.postValue(0)
                } else {
                    liveQuantity.postValue(it.toString().getOnlyDigits().toLong())
                }
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

            btnCancel.onClick {
                dismiss()
            }
        }
    }

    private fun String.getOnlyDigits(): String {
        val s = this.filter { it.isDigit() }
        return if (s.isEmpty()) "0" else s
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        liveQuantity.observe(requireActivity(), { quantity ->
            val newWholesale = product.priceWholesale * quantity
            val newMin = product.priceMin * quantity
            val newMax = product.priceMax * quantity

            binding.apply {
                tvWholesale.text =
                    context?.getString(R.string.wholesale_price_text, newWholesale.toSumFormat)
                tvMin.text = context?.getString(R.string.min_price_text, newMin.toSumFormat)
                tvMax.text = context?.getString(R.string.max_price_text, newMax.toSumFormat)
            }
        })
    }

    private var onItemClick: (quantity: Int, summa: String) -> Unit = { _, _ -> }
    fun onItemClickListener(onItemClick: (quantity: Int, summa: String) -> Unit) {
        this.onItemClick = onItemClick
    }
}
