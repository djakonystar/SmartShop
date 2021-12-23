package uz.texnopos.elektrolife.ui.newproduct

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.*
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.newproduct.Amount
import uz.texnopos.elektrolife.data.model.newproduct.Categories
import uz.texnopos.elektrolife.data.model.newproduct.Product
import uz.texnopos.elektrolife.databinding.ActionBarProductNewBinding
import uz.texnopos.elektrolife.databinding.FragmentProductNewBinding
import uz.texnopos.elektrolife.settings.Settings

class NewProductFragment : Fragment(R.layout.fragment_product_new) {
    private lateinit var binding: FragmentProductNewBinding
    private lateinit var abBinding: ActionBarProductNewBinding
    private lateinit var navController: NavController
    private lateinit var groupAdapter: ArrayAdapter<String>
    private val viewModel: NewProductViewModel by viewModel()
    private val settings: Settings by inject()
    private var mutableList: MutableList<Categories> = mutableListOf()
    private var categoryName: MutableList<String> = mutableListOf()
    private var categoryId = 0
    private val amount = MediatorLiveData<Amount>().apply { value = Amount() }
    private var liveCostPrice = MutableLiveData<Double>()
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
        abBinding = ActionBarProductNewBinding.bind(view)
        navController = findNavController()

        observeCostChange()
        setupObserver()
        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_product)
            tvRate.text =
                context?.getString(R.string.dollar_rate_text, settings.dollarRate.toString())
            btnHome.onClick {
                navController.popBackStack()
            }
        }
        viewModel.getCategories()

        binding.apply {
            etWholesalePrice.addTextChangedListener(MaskWatcherPriceDecimal(etWholesalePrice))
            etMinPrice.addTextChangedListener(MaskWatcherPayment(etMinPrice))
            etMaxPrice.addTextChangedListener(MaskWatcherPayment(etMaxPrice))
            etProductQuantity.addTextChangedListener(MaskWatcherNothing(etProductQuantity))

            etProductName.addTextChangedListener {
                tilProductName.isErrorEnabled = false
            }
            etProductQuantity.addTextChangedListener {
                tilProductQuantity.isErrorEnabled = false
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
            }

            actSpinner.setOnItemClickListener { _, _, i, _ ->
                tilSpinner.isErrorEnabled = false
                categoryId = mutableList[i].categoryId
                wholesalePercent = mutableList[i].percentWholesale
                minPercent = mutableList[i].percentMin
                maxPercent = mutableList[i].percentMax
            }

            etCostPrice.addTextChangedListener(MaskWatcherPriceDecimal(etCostPrice))
            etCostPrice.doOnTextChanged { it, _, _, _ ->
                if (it.isNullOrEmpty()) {
                    liveCostPrice.postValue(0.0)
                } else {
                    liveCostPrice.postValue(
                        it.toString().getOnlyDigits().toDouble()
                    )
                }
            }

            btnAddProduct.onClick {
                val productName = etProductName.text.toString()
                val costPrice = etCostPrice.text.toString().filter { c -> c.isDigit() }
                val productQuantity = etProductQuantity.text.toString().filter { q -> q.isDigit() }
                val branch = etBranchName.text.toString()
                val wholesalePrice = etWholesalePrice.text.toString().filter { w -> w.isDigit() }
                val minPrice = etMinPrice.text.toString().filter { min -> min.isDigit() }
                val maxPrice = etMaxPrice.text.toString().filter { max -> max.isDigit() }

                if (categoryId != 0 && productName.isNotEmpty() && costPrice.isNotEmpty()
                    && wholesalePrice.isNotEmpty() && minPrice.isNotEmpty() && maxPrice.isNotEmpty() && productQuantity.isNotEmpty()
                ) {
                    // todo post request for add new product
                    viewModel.createProduct(
                        Product(
                            categoryId,
                            branch,
                            productName,
                            costPrice.toDouble(),
                            wholesalePrice.toDouble(),
                            minPrice.toInt(),
                            maxPrice.toInt(),
                            productQuantity.toInt()
                        )
                    )
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
                    if (productQuantity.isEmpty()) {
                        tilProductQuantity.error = context?.getString(R.string.required_field)
                    }
                    if (costPrice.isEmpty()) {
                        tilCostPrice.error = context?.getString(R.string.required_field)
                    }
                }
            }
            ivAddCategory.onClick {
                navController.navigate(R.id.action_newProductFragment_to_newCategoryFragment)
            }
        }
    }

    private fun String.getOnlyDigits(): String {
        val s = this.filter { it.isDigit() || it == '.' }
        return if (s.isEmpty()) "0" else s
    }

    private fun merge() {
        amount.addSource(liveCostPrice) {
            val previous = amount.value
            amount.value = previous?.copy(wholesalePrice = it)
            amount.value = previous?.copy(minPrice = it.toLong())
            amount.value = previous?.copy(maxPrice = it.toLong())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeCostChange() {
        liveCostPrice.observe(requireActivity(), {
            val price = it
            binding.apply {
                val wholesalePrice = (wholesalePercent / 100.0 + 1) * price
                val minPrice = ((minPercent / 100.0 + 1) * price * settings.dollarRate).toLong()
                val maxPrice = ((maxPercent / 100.0 + 1) * price * settings.dollarRate).toLong()

                etWholesalePrice.setText("%.3f".format(wholesalePrice).replace(',', '.'))
                etMinPrice.setText(rounding(minPrice).toSumFormat)
                etMaxPrice.setText(rounding(maxPrice).toSumFormat)
            }
        })
    }

    private fun rounding(price: Long) = ((price + 500) / 1000) * 1000

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            scrollView.isEnabled = !loading
        }
    }

    private fun setupObserver() {
        viewModel.categories.observe(viewLifecycleOwner, {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        mutableList = it.data.payload.toMutableList()
                        mutableList.forEach { data ->
                            if (!categoryName.contains(data.name)) categoryName.add(data.name)
                        }
                        groupAdapter =
                            ArrayAdapter(requireContext(), R.layout.item_spinner, categoryName)
                        binding.actSpinner.setAdapter(groupAdapter)
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        })

    }

    private fun setupObserverCreatedProduct() {
        viewModel.createProduct.observe(viewLifecycleOwner, {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        val alertDialog = AlertDialog.Builder(requireContext())
                        alertDialog.setTitle(context?.getString(R.string.success))
                        alertDialog.setMessage(context?.getString(R.string.product_added_successfully))
                        alertDialog.show()
                        binding.apply {
                            actSpinner.text.clear()
                            etProductName.text!!.clear()
                            etMinPrice.text!!.clear()
                            etMaxPrice.text!!.clear()
                            etCostPrice.text!!.clear()
                            etWholesalePrice.text!!.clear()
                            etBranchName.text!!.clear()
                            etProductQuantity.text!!.clear()
                            categoryId = 0
                        }
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        })
    }
}
