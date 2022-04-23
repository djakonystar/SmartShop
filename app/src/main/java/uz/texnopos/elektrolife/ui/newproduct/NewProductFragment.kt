package uz.texnopos.elektrolife.ui.newproduct

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.category.CategoryResponse
import uz.texnopos.elektrolife.data.model.newproduct.Price
import uz.texnopos.elektrolife.data.model.newproduct.Product
import uz.texnopos.elektrolife.data.model.newproduct.Warehouse
import uz.texnopos.elektrolife.data.model.warehouse.WarehouseItem
import uz.texnopos.elektrolife.databinding.ActionBarProductNewBinding
import uz.texnopos.elektrolife.databinding.FragmentProductNewBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.currency.CurrencyViewModel
import uz.texnopos.elektrolife.ui.dialog.TransactionDialog
import uz.texnopos.elektrolife.ui.newsale.CategoryViewModel
import uz.texnopos.elektrolife.ui.warehouse.WarehouseViewModel

class NewProductFragment : Fragment(R.layout.fragment_product_new) {
    private lateinit var binding: FragmentProductNewBinding
    private lateinit var abBinding: ActionBarProductNewBinding
    private lateinit var navController: NavController
    private lateinit var categoriesAdapter: ArrayAdapter<String>
    private val viewModel: NewProductViewModel by viewModel()
    private val warehouseViewModel: WarehouseViewModel by viewModel()
    private val currencyViewModel: CurrencyViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val settings: Settings by inject()
    private var mutableList: MutableList<CategoryResponse> = mutableListOf()
    private var categoryName: MutableList<String> = mutableListOf()
    private var categoryId = -1
    private var liveCostPrice = MutableLiveData<Double>()
    private var categoryNameLiveData = MutableLiveData<String>()
    private var wholesalePercent: Double = 0.0
    private var minPercent: Double = 0.0
    private var maxPercent: Double = 0.0
    private var list: MutableSet<String> = mutableSetOf()
    private var listProducts: MutableMap<String, WarehouseItem> = mutableMapOf()
    private var mapOfCurrency: MutableMap<Int, String> = mutableMapOf()
    private var productName = ""
    private val measureUnitsList = Constants.getUnits()
    private var unitId: Int = 1
    private var measureUnitLiveData: MutableLiveData<Int> = MutableLiveData(1)
    private var currencyIds = mutableListOf(-1, -1, -1, -1)
    private var currencyRate = mutableMapOf<String, Double>()
    private var currencyLiveData = MutableLiveData<Pair<Double, MutableList<Int>>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProductNewBinding.bind(view)
        abBinding = ActionBarProductNewBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.new_product)
            tvRate.text =
                context?.getString(R.string.dollar_rate_text, settings.usdToUzs.toString())
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                currencyViewModel.getCurrency()
                categoryViewModel.getCategories()
            }

            etProductName.addTextChangedListener {
                tilProductName.isErrorEnabled = false
                if (it.toString().isNotEmpty()) {
                    list.clear()
                    warehouseViewModel.warehouseProducts(it.toString())
                }
            }

            etProductName.setOnItemClickListener { adapterView, _, i, _ ->
                productName = adapterView.getItemAtPosition(i).toString()
                val warehouseItem = listProducts.getValue(productName)
                val dialog = TransactionDialog(warehouseItem.product)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                dialog.setOnDismissListener {
                    etProductName.text.clear()
                }
            }

            etProductQuantity.addTextChangedListener {
                tilProductQuantity.isErrorEnabled = false
            }

            etCostPrice.filterForDouble
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

            categoriesAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, categoryName)
            actCategory.setAdapter(categoriesAdapter)
            actCategory.setOnFocusChangeListener { _, b ->
                if (b) {
                    actCategory.showDropDown()
                    tilCategory.isErrorEnabled = false
                }
            }

            actCategory.setOnItemClickListener { _, _, i, _ ->
                tilCategory.isErrorEnabled = false
                categoryId = mutableList[i].id
                wholesalePercent = mutableList[i].wholePercent
                minPercent = mutableList[i].minPercent
                maxPercent = mutableList[i].maxPercent
            }

            actCategory.doOnTextChanged { text, _, _, _ ->
                categoryNameLiveData.postValue(text.toString())
            }

            val unitAdapter =
                ArrayAdapter(requireContext(), R.layout.item_spinner, measureUnitsList)
            actMeasureUnit.setAdapter(unitAdapter)
            actMeasureUnit.threshold = 200
            actMeasureUnit.setText(measureUnitsList[0])
            unitId = 1
            measureUnitLiveData.postValue(unitId)

            actMeasureUnit.setOnItemClickListener { _, _, i, _ ->
                tilMeasureUnit.isErrorEnabled = false
                unitId = i + 1
                measureUnitLiveData.postValue(unitId)
            }

            etCostPrice.doOnTextChanged { it, _, _, _ ->
                liveCostPrice.postValue(it.toString().toDouble)
            }

            actCostCurrency.threshold = 100
            actWholesaleCurrency.threshold = 100
            actMinCurrency.threshold = 100
            actMaxCurrency.threshold = 100

            actCostCurrency.setOnItemClickListener { _, _, i, _ ->
                tilCostCurrency.isErrorEnabled = false
                currencyIds[0] = i + 1
            }

            actWholesaleCurrency.setOnItemClickListener { _, _, i, _ ->
                tilWholesaleCurrency.isErrorEnabled = false
                currencyIds[1] = i + 1
            }

            actMinCurrency.setOnItemClickListener { _, _, i, _ ->
                tilMinCurrency.isErrorEnabled = false
                currencyIds[2] = i + 1
            }

            actMaxCurrency.setOnItemClickListener { _, _, i, _ ->
                tilMaxCurrency.isErrorEnabled = false
                currencyIds[3] = i + 1
            }

            btnAddProduct.onClick {
                val productName = etProductName.text.toString()
                val costPrice = etCostPrice.text.toString().toDouble
                val productQuantity = etProductQuantity.text.toString().toDouble
                val brand = etBrand.text.toString()
                val wholesalePrice = etWholesalePrice.text.toString().toDouble
                val minPrice = etMinPrice.text.toString().toDouble
                val maxPrice = etMaxPrice.text.toString().toDouble

                if (categoryId != -1 && productName.isNotEmpty() && costPrice != 0.0 &&
                    wholesalePrice != 0.0 && minPrice != 0.0 && maxPrice != 0.0 &&
                    checkCurrencies && unitId != -1
                ) {
                    viewModel.createProduct(
                        Product(
                            categoryId = categoryId,
                            name = productName,
                            brand = brand,
                            costPrice = Price(currencyIds[0], costPrice),
                            wholesalePrice = Price(currencyIds[1], wholesalePrice),
                            minPrice = Price(currencyIds[2], minPrice),
                            maxPrice = Price(currencyIds[3], maxPrice),
                            warehouse = Warehouse(unitId, productQuantity)
                        )
                    )
                } else {
                    if (categoryId == -1) {
                        tilCategory.error = context?.getString(R.string.required_field)
                    }
                    if (productName.isEmpty()) {
                        tilProductName.error = context?.getString(R.string.required_field)
                    }
                    if (unitId == -1) {
                        tilMeasureUnit.error = getString(R.string.required_field)
                    }
                    if (costPrice == 0.0) {
                        tilCostPrice.error = context?.getString(R.string.required_field)
                    }
                    if (wholesalePrice == 0.0) {
                        tilWholesalePrice.error = context?.getString(R.string.required_field)
                    }
                    if (minPrice == 0.0) {
                        tilMinPrice.error = context?.getString(R.string.required_field)
                    }
                    if (maxPrice == 0.0) {
                        tilMaxPrice.error = context?.getString(R.string.required_field)
                    }
                    if (productQuantity == 0.0) {
                        tilProductQuantity.error = context?.getString(R.string.required_field)
                    }
                    if (!checkCurrencies) {
                        currencyIds.forEachIndexed { index, element ->
                            if (element == -1) {
                                when (index) {
                                    0 -> tilCostCurrency.error = getString(R.string.required_field)
                                    1 -> tilWholesaleCurrency.error =
                                        getString(R.string.required_field)
                                    2 -> tilMinCurrency.error = getString(R.string.required_field)
                                    3 -> tilMaxCurrency.error = getString(R.string.required_field)
                                }
                            }
                        }
                    }
                }
            }
            ivAddCategory.onClick {
                navController.navigate(R.id.action_newProductFragment_to_newCategoryFragment)
            }
        }

        currencyViewModel.getCurrency()
        categoryViewModel.getCategories()
        setUpObservers()
    }

    private fun rounding(price: Long): Long {
        val costText = binding.etCostPrice.text.toString()
        val cost = if (costText == "." || costText.isEmpty()) 0.0 else costText.toDouble()
        val sum = if (cost < 1) 100 else 500
        val divider = if (cost < 1) 100 else 1000
        return ((price + sum) / divider) * divider
    }

    private val checkCurrencies: Boolean
        get() {
            for (e in currencyIds) if (e == -1)
                return false
            return true
        }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            scrollView.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        measureUnitLiveData.observe(viewLifecycleOwner) {
            when (it) {
                // pcs
                1 -> {
                    binding.etProductQuantity.setBlockFilter("-.,")
                    val oldText = binding.etProductQuantity.text.toString()
                    binding.etProductQuantity.setText(oldText.substringBefore('.'))
                    binding.etProductQuantity.setSelection(binding.etProductQuantity.length())
                }
                // tonne, kg, gr, meter, sm, litre
                else -> binding.etProductQuantity.filterForDouble
            }
        }

        liveCostPrice.observe(viewLifecycleOwner) {
            val price = it
            binding.apply {
                val wholesalePrice = ((wholesalePercent / 100.0 + 1) * price)
                val minPrice = ((minPercent / 100.0 + 1) * price * settings.usdToUzs)
                val maxPrice = ((maxPercent / 100.0 + 1) * price * settings.usdToUzs)

                etWholesalePrice.setText(wholesalePrice format 2)
                etMinPrice.setText(minPrice format 2)
                etMaxPrice.setText(maxPrice format 2)

                if (price == 0.0) {
                    etWholesalePrice.text!!.clear()
                    etMinPrice.text!!.clear()
                    etMaxPrice.text!!.clear()
                }
            }
        }

        currencyLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                val price = it.first

                val rateForWholesale = if (it.second[0] != it.second[1]) {
                    currencyRate.getValue(
                        "${mapOfCurrency.getValue(it.second[0])}to${
                            mapOfCurrency.getValue(it.second[1])
                        }"
                    )
                } else 1.0
                val rateForMin = if (it.second[0] != it.second[2]) {
                    currencyRate.getValue(
                        "${mapOfCurrency.getValue(it.second[0])}to${
                            mapOfCurrency.getValue(it.second[2])
                        }"
                    )
                } else 1.0
                val rateForMax = if (it.second[0] != it.second[3]) {
                    currencyRate.getValue(
                        "${mapOfCurrency.getValue(it.second[0])}to${
                            mapOfCurrency.getValue(it.second[3])
                        }"
                    )
                } else 1.0


                val wholesalePrice = ((wholesalePercent / 100.0 + 1) * price * rateForWholesale)
                val minPrice = ((minPercent / 100.0 + 1) * price * rateForMin)
                val maxPrice = ((maxPercent / 100.0 + 1) * price * rateForMax)

                etWholesalePrice.setText(wholesalePrice format 2)
                etMinPrice.setText(minPrice format 2)
                etMaxPrice.setText(maxPrice format 2)

                if (price == 0.0) {
                    etWholesalePrice.text!!.clear()
                    etMinPrice.text!!.clear()
                    etMaxPrice.text!!.clear()
                }
            }
        }

        categoryNameLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                tilCostPrice.isEnabled = it.isNotEmpty()
                tilCostCurrency.isEnabled = it.isNotEmpty()
                tilWholesalePrice.isEnabled = it.isNotEmpty()
                tilWholesaleCurrency.isEnabled = it.isNotEmpty()
                tilMinPrice.isEnabled = it.isNotEmpty()
                tilMinCurrency.isEnabled = it.isNotEmpty()
                tilMaxPrice.isEnabled = it.isNotEmpty()
                tilMaxCurrency.isEnabled = it.isNotEmpty()
            }
        }

        currencyViewModel.currency.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    binding.apply {
                        setLoading(false)
                        mapOfCurrency.clear()
                        it.data!!.forEach { currency ->
                            mapOfCurrency[currency.id] = currency.code
                            if (currency.code == "USD") {
                                currencyIds[0] = currency.id
                                currencyIds[1] = currency.id
                            } else if (currency.code == "UZS") {
                                currencyIds[2] = currency.id
                                currencyIds[3] = currency.id
                            }

                            currency.rate.forEach { toCurrency ->
                                currencyRate["${currency.code}to${toCurrency.code}"] =
                                    toCurrency.rate.toDouble()
                            }
                        }

                        val currencyList = mapOfCurrency.values.toList()
                        val currencyAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.item_spinner,
                            currencyList
                        )
                        actCostCurrency.setAdapter(currencyAdapter)
                        actWholesaleCurrency.setAdapter(currencyAdapter)
                        actMinCurrency.setAdapter(currencyAdapter)
                        actMaxCurrency.setAdapter(currencyAdapter)

                        actCostCurrency.setText(currencyList[currencyIds[0] - 1])
                        actWholesaleCurrency.setText(currencyList[currencyIds[1] - 1])
                        actMinCurrency.setText(currencyList[currencyIds[2] - 1])
                        actMaxCurrency.setText(currencyList[currencyIds[3] - 1])
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        categoryViewModel.categories.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    mutableList = it.data!!.toMutableList()
                    mutableList.forEach { category ->
                        if (!categoryName.contains(category.name)) categoryName.add(category.name)
                    }
                    categoriesAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_spinner, categoryName)
                    binding.actCategory.setAdapter(categoriesAdapter)
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        warehouseViewModel.warehouseProducts.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data!!.forEach { warehouseItem ->
                        list.add(warehouseItem.product.name)
                        if (!listProducts.contains(warehouseItem.product.name)) listProducts[warehouseItem.product.name] =
                            warehouseItem
                    }
                    binding.apply {
                        if (etProductName.text.isEmpty()) {
                            list.clear()
                            val arrayAdapter = ArrayAdapter(
                                requireContext(),
                                R.layout.item_spinner,
                                list.toMutableList()
                            )
                            etProductName.setAdapter(arrayAdapter)
                            etProductName.dismissDropDown()
                        } else {
                            val arrayAdapter = ArrayAdapter(
                                requireContext(),
                                R.layout.item_spinner,
                                list.toMutableList()
                            )
                            etProductName.setAdapter(arrayAdapter)
                            etProductName.showDropDown()
                            if (list.size == 1 && etProductName.text.toString() == list.firstOrNull()) {
                                etProductName.dismissDropDown()
                            }
                        }
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        viewModel.product.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    // TODO: Print qrcode
                    showSuccess(getString(R.string.product_added_successfully))
                        .setOnPositiveButtonClickListener {
                            navController.popBackStack()
                        }
                    binding.apply {
                        actCategory.text.clear()
                        categoryId = -1
                        etProductName.text!!.clear()
                        etBrand.text!!.clear()
                        etProductQuantity.text!!.clear()
                        actMeasureUnit.setText(measureUnitsList[0])
                        unitId = 1
                        etCostPrice.text!!.clear()
                        actCostCurrency.text.clear()
                        etWholesalePrice.text!!.clear()
                        actWholesaleCurrency.text.clear()
                        etMinPrice.text!!.clear()
                        actMinCurrency.text.clear()
                        etMaxPrice.text!!.clear()
                        actMaxCurrency.text.clear()
                        currencyIds = mutableListOf(-1, -1, -1, -1)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
