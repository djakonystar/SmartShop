package uz.texnopos.electrolightwarehouse.ui.newproduct

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MaskWatcherPayment
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.newProduct.Amount
import uz.texnopos.electrolightwarehouse.data.newProduct.Categories
import uz.texnopos.electrolightwarehouse.data.newProduct.Product
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentProductNewBinding

class NewProductFragment : Fragment(R.layout.fragment_product_new) {
    private lateinit var binding: FragmentProductNewBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private lateinit var groupAdapter: ArrayAdapter<String>
    private val viewModel: NewProductViewModel by viewModel()
    var mutableList: MutableList<Categories> = mutableListOf()
    private var categoryName: MutableList<String> = mutableListOf()
    private var categoryId = 0
    private val amount = MediatorLiveData<Amount>().apply { value = Amount() }
    private var liveCostPrice = MutableLiveData<Long>()
    private var wholesalePercent: Int = 0
    private var minPercent: Int = 0
    private var maxPercent: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        merge()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProductNewBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        observeCostChange()
        setupObserver()
        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_product)
            btnHome.onClick {
                navController.popBackStack()
            }
        }
        viewModel.getCategories()

        binding.apply {
            etWholesalePrice.addTextChangedListener(MaskWatcherPayment(etWholesalePrice))
            etMinPrice.addTextChangedListener(MaskWatcherPayment(etMinPrice))
            etMaxPrice.addTextChangedListener(MaskWatcherPayment(etMaxPrice))

            etProductName.addTextChangedListener {
                tilProductName.isErrorEnabled = false
            }
            etCostPrice.addTextChangedListener {
                tilCostPrice.isErrorEnabled = false
            }
            etWholesalePrice.addTextChangedListener {
                tilWholesalePrice.isErrorEnabled = false
            }
            etMinPrice.addTextChangedListener {
                tilMinPrice.isErrorEnabled = false
            }
            etMaxPrice.addTextChangedListener {
                tilMaxPrice.isErrorEnabled = false
            }

            groupAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, categoryName)
            actSpinner.setAdapter(groupAdapter)
            actSpinner.setOnFocusChangeListener { _, b ->
                if (b) {
                    actSpinner.showDropDown()
                    tilSpinner.isErrorEnabled = false
                }
                tilSpinner.isEnabled = false
                // todo test
            }

            actSpinner.setOnItemClickListener { _, _, i, _ ->
                categoryId = mutableList[i].categoryId
            }

            etCostPrice.addTextChangedListener(MaskWatcherPayment(etCostPrice))
            etCostPrice.doOnTextChanged { it, _, _, _ ->
                if (it.isNullOrEmpty()) {
                    liveCostPrice.postValue(0)
                } else {
                    liveCostPrice.postValue(it.toString().getOnlyDigits().toLong())
                }
            }

            btnAddProduct.onClick {
                val productName = etProductName.text.toString()
                val costPrice = etCostPrice.text.toString().filter { c->c.isDigit() }
                val branch = etBranchName.text.toString()
                val wholesalePrice = etWholesalePrice.text.toString().filter { w->w.isDigit() }
                val minPrice = etMinPrice.text.toString().filter { min->min.isDigit() }
                val maxPrice = etMaxPrice.text.toString().filter { max->max.isDigit() }

                if (categoryId != 0 && productName.isNotEmpty() && costPrice.isNotEmpty()
                    && wholesalePrice.isNotEmpty() && minPrice.isNotEmpty() && maxPrice.isNotEmpty()
                ) {
                    // todo post request for add new product
                    viewModel.createProduct(Product(categoryId,branch,productName, costPrice.toInt(),
                        wholesalePrice.toInt(),minPrice.toInt(),maxPrice.toInt()))
                    setupObserverCreatedProduct()
                } else {
                    if (categoryId == 0) {
                        tilSpinner.error = context?.getString(R.string.required_field)
                    }
                    if (productName.isEmpty()) {
                        tilProductName.error = context?.getString(R.string.required_field)
                    }
                    if (wholesalePrice.isEmpty()) {
                        tilWholesalePrice.error = context?.getString(R.string.required_field)
                    }
                    if (minPrice.isEmpty()) {
                        tilMinPrice.error = context?.getString(R.string.required_field)
                    }
                    if (maxPrice.isEmpty()) {
                        tilMaxPrice.error = context?.getString(R.string.required_field)
                    }
                }
            }
            ivAddCategory.onClick {
                navController.navigate(R.id.action_newProductFragment_to_newCategoryFragment)
            }

        }

    }

    private fun String.getOnlyDigits(): String {
        var s = ""
        this.forEach { if (it.isDigit()) s += it }
        return if (s.isEmpty()) "0" else s
    }

    private fun merge() {
        amount.addSource(liveCostPrice) {
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

    private fun observeCostChange() {
        liveCostPrice.observe(requireActivity(), {
            val price = it
            binding.apply {
                // TODO: change to extension
                etWholesalePrice.setText(
                    (wholesalePercent * price.toString().toLong() / 100L + price.toString()
                        .toLong()).changeFormat()
                )
                etMaxPrice.setText(
                    (maxPercent * price.toString().toLong() / 100L + price.toString()
                        .toLong()).changeFormat()
                )
                etMinPrice.setText(
                    (minPercent * price.toString().toLong() / 100L + price.toString()
                        .toLong()).changeFormat()
                )
            }
        })
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            scrollView.isEnabled = !loading
        }
    }
    private fun setupObserver(){
        viewModel.categories.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{setLoading(true)}
                ResourceState.SUCCESS->{setLoading(false)
                    if (it.data!!.successful){
                        mutableList = it.data.payload.toMutableList()
                        mutableList.forEach { data->
                            wholesalePercent = data.percentWholesale
                            minPercent = data.percentMin
                            maxPercent = data.percentMax
                            if (!categoryName.contains(data.name))categoryName.add(data.name)
                        }
                    }else{
                        Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    }
                }
                ResourceState.ERROR->{setLoading(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()}
            }
        })

    }
    private fun setupObserverCreatedProduct(){
        viewModel.createProduct.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{setLoading(true)}
                ResourceState.SUCCESS->{setLoading(false)
                    if (it.data!!.successful){
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle("Muvaffaqiyatli!")
                        alertDialog.setMessage("Tovar muvaffaqiyatli qoshildi!")
                        alertDialog.show()
                        binding.apply {
                            actSpinner.text.clear()
                            etProductName.text!!.clear()
                            etMinPrice.text!!.clear()
                            etMaxPrice.text!!.clear()
                            etCostPrice.text!!.clear()
                            etWholesalePrice.text!!.clear()
                            etBranchName.text!!.clear()
                            categoryId = 0
                        }
                    }else{
                        Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    }
                }
                ResourceState.ERROR->{setLoading(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()}
            }
        })
    }
}
