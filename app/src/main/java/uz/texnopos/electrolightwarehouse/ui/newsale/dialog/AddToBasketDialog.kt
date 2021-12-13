package uz.texnopos.electrolightwarehouse.ui.newsale.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPrice
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Amount
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.DialogAddToBasketBinding

class AddToBasketDialog(private val product: Product):DialogFragment() {
    private var _binding:DialogAddToBasketBinding? = null
    private val binding get() = _binding!!
    private val amount = MediatorLiveData<Amount>().apply { value = Amount() }
    private var livePrice = MutableLiveData<Long>()
    private var liveQuantity = MutableLiveData<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddToBasketBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
//        merge()
        binding.apply {
            etSumma.addTextChangedListener(MaskWatcherPrice(etSumma))
        }
        binding.apply {
//            etSumma.doOnTextChanged { it, _, _, _ ->
//                if (it.isNullOrEmpty()){
//                    livePrice.postValue(0)
//                }else{
//                    livePrice.postValue(it.toString().getOnlyDigits().toLong())
//                }
//            }
            etQuantity.doOnTextChanged { it, _, _, _ ->
                if (it.isNullOrEmpty()){
                    liveQuantity.postValue(0)
                }else{
                    liveQuantity.postValue(it.toString().getOnlyDigits().toLong())
                }
            }

            binding.btnAdd.onClick {
                if (etQuantity.text.isNotEmpty() && etSumma.text.isNotEmpty()){
                    val quantity = etQuantity.text.toString().getOnlyDigits()
                    val summa = etSumma.text.toString().getOnlyDigits()
                    onItemClick.invoke(quantity.toInt(), summa)
                    dismiss()
                }else{
                    Toast.makeText(context, getString(R.string.fill_the_fields), Toast.LENGTH_SHORT).show()
                }
            }

            binding.btnCancel.onClick {
                dismiss()
            }
        }
    }

    private fun String.getOnlyDigits(): String {
        var s = ""
        this.forEach { if (it.isDigit()) s += it }
        return if (s.isEmpty()) "0" else s
    }
    private fun merge() {
        amount.addSource(livePrice) {
            val previous = amount.value
            amount.value = previous?.copy( sum = it)
        }
        amount.addSource(liveQuantity) {
            val previous = amount.value
            amount.value = previous?.copy(quantity = it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observe(){
        liveQuantity.observe(requireActivity(),{
            val quantity = it
            binding.apply {
                tvWholesale.text = "Ulgurji: " + (product.priceMin * quantity).changeFormat()
                tvMin.text =  "Min: " +(product.priceWholesale * quantity).changeFormat()
                tvMax.text =  "Max: " +(product.priceMax * quantity).changeFormat()
            }
        })
    }

    private fun Long.changeFormat(): String {
        val num = this.toLong().toString()
        var s = ""
        val sz = num.length
        for (i in 0 until sz) {
            if (i != 0 && (i - sz % 3) % 3 == 0) s += ' '
            s += num[i]
        }
        return "$s UZS"
    }

    private var onItemClick: (quantity:Int, summa:String) -> Unit = { _, _->}
    fun onItemClickListener(onItemClick: (quantity: Int, summa: String) -> Unit) {
        this.onItemClick = onItemClick
    }
}