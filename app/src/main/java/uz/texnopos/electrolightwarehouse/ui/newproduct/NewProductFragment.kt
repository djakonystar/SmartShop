package uz.texnopos.electrolightwarehouse.ui.newproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPayment
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.newProduct.Amount
import uz.texnopos.electrolightwarehouse.data.newProduct.Categories
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentProductNewBinding

class NewProductFragment(): Fragment(R.layout.fragment_product_new) {

    private var _binding: FragmentProductNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var abBinding: ActionBarBinding
    private lateinit var groupAdapter: ArrayAdapter<String>
    var mutableList: MutableList<Categories> = mutableListOf()
    private var categoryName: MutableList<String> = mutableListOf()
    private var categoryId = 0
    private val amount = MediatorLiveData<Amount>().apply{ value = Amount()}
    private var liveCostPrice = MutableLiveData<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductNewBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        abBinding = ActionBarBinding.bind(view)
        observe()
        merge()
        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_product)
            btnHome.onClick {
                navController.popBackStack()
            }
        }
        binding.apply {
            etMaxPrice.addTextChangedListener(MaskWatcherPayment(etMaxPrice))
            etMinPrice.addTextChangedListener(MaskWatcherPayment(etMinPrice))
            etWholesalePrice.addTextChangedListener(MaskWatcherPayment(etWholesalePrice))

            groupAdapter = ArrayAdapter(requireContext(),R.layout.item_spinner,categoryName)
            actSpinner.setAdapter(groupAdapter)
            actSpinner.setOnFocusChangeListener{_,b->
                if (b){
                    actSpinner.showDropDown()
                }
                tilSpinner.isEnabled = false
            }
            actSpinner.setOnItemClickListener {_, _, i, _->
                categoryId = mutableList[i].categoryId
            }
            etCostPrice.addTextChangedListener(MaskWatcherPayment(etCostPrice))
        etCostPrice.doOnTextChanged { it,_,_,_->
            if (it.isNullOrEmpty()){
                liveCostPrice.postValue(0)
            }else{
                liveCostPrice.postValue(it.toString().getOnlyDigits().toLong())
            }
        }
            btnAddProduct.onClick {
                if (categoryId!=0 &&etProductName.text!!.isNotEmpty()&&etCostPrice.text!!.isNotEmpty()){

                }else{
                    Toast.makeText(requireContext(), "Bosh joylarni toltiring", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun String.getOnlyDigits(): String {
        var s = ""
        this.forEach { if (it.isDigit()) s += it }
        return if (s.isEmpty()) "0" else s
    }

    private fun merge(){
        amount.addSource(liveCostPrice){
            val previos = amount.value
            amount.value = previos?.copy(wholesalePrice = it)
            amount.value = previos?.copy(minPrice = it)
            amount.value = previos?.copy(maxPrice = it)
        }
    }


    private fun Long.changeFormat(): String {
        val num = this.toLong().toString()
        var s = ""
        val sz = num.length
        for (i in 0 until sz) {
            if (i != 0 && (i - sz % 3) % 3 == 0) s += ' '
            s += num[i]
        }
        return "$s uzs"
    }
    private fun observe(){
        liveCostPrice.observe(requireActivity(),{
            val price = it
            binding.apply {
                etWholesalePrice.setText((10*price.toString().toLong()/100L+price.toString().toLong()).changeFormat())
                etMaxPrice.setText((20*price.toString().toLong()/100L+price.toString().toLong()).changeFormat())
                etMinPrice.setText((15*price.toString().toLong()/100L+price.toString().toLong()).changeFormat())
            }
        })
    }

}