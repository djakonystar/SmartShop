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
import uz.texnopos.elektrolife.data.model.newproduct.TransactionTransfer
import uz.texnopos.elektrolife.data.model.newproduct.Warehouse
import uz.texnopos.elektrolife.data.model.warehouse.WarehouseItem
import uz.texnopos.elektrolife.databinding.ActionBarProductNewBinding
import uz.texnopos.elektrolife.databinding.FragmentProductNewBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.currency.CurrencyViewModel
import uz.texnopos.elektrolife.ui.dialog.TransactionDialog
import uz.texnopos.elektrolife.ui.newsale.CategoryViewModel
import uz.texnopos.elektrolife.ui.qrscanner.QrScannerFragment
import site.texnopos.djakonystar.suminputmask.SumInputMask

class NewProductFragment : Fragment(R.layout.fragment_product_new) {
    private lateinit var binding: FragmentProductNewBinding
    private lateinit var abBinding: ActionBarProductNewBinding
    private lateinit var navController: NavController
    private lateinit var categoriesAdapter: ArrayAdapter<String>
    private val viewModel: NewProductViewModel by viewModel()
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
    private var productsList: MutableSet<String> = mutableSetOf()
    private var productsMap: MutableMap<String, WarehouseItem> = mutableMapOf()
    private var mapOfCurrency: MutableMap<Int, String> = mutableMapOf()
    private var currencyIds = mutableListOf(-1, -1, -1, -1)
    private var mapOfRates = mutableMapOf<String, Double>()
    private var productName = ""
    private lateinit var measureUnitsList: List<String>
    private var unitId: Int = 1
    private var measureUnitLiveData: MutableLiveData<Int> = MutableLiveData(1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProductNewBinding.bind(view)
        abBinding = ActionBarProductNewBinding.bind(view)
        navController = findNavController()
        measureUnitsList = Constants.getUnits(requireContext())

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
                    productsList.clear()
                    viewModel.getWarehouseProducts(it.toString())
                }
            }

            etProductName.setOnItemClickListener { adapterView, _, i, _ ->
                productName = adapterView.getItemAtPosition(i).toString()
                val warehouseItem = productsMap.getValue(productName)
                val transaction = TransactionTransfer(
                    warehouseItem.product.id,
                    warehouseItem.product.name,
                    warehouseItem.count,
                    warehouseItem.unit.id,
                    Price(
                        warehouseItem.product.costPrice.currencyId,
                        warehouseItem.product.costPrice.price
                    )
                )
                val dialog = TransactionDialog(transaction)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                dialog.setOnDismissListener {
                    etProductName.text.clear()
                }
            }

            ivQrScanner.onClick {
                navController.navigate(
                    NewProductFragmentDirections.actionNewProductFragmentToQrScannerFragment(
                        QrScannerFragment.POST_TRANSACTION
                    )
                )
            }

            etProductQuantity.addTextChangedListener {
                tilProductQuantity.isErrorEnabled = false
            }

            SumInputMask(etCostPrice, etWholesalePrice, etMinPrice, etMaxPrice)

            etCostPrice.addTextChangedListener {
                tilCostPrice.isErrorEnabled = false
                calculate()
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

            actCostCurrency.threshold = 100
            actWholesaleCurrency.threshold = 100
            actMinCurrency.threshold = 100
            actMaxCurrency.threshold = 100

            actCostCurrency.setOnItemClickListener { _, _, i, _ ->
                tilCostCurrency.isErrorEnabled = false
                currencyIds[0] = i + 1
                calculate()
            }

            actWholesaleCurrency.setOnItemClickListener { _, _, i, _ ->
                tilWholesaleCurrency.isErrorEnabled = false
                currencyIds[1] = i + 1
                calculate()
            }

            actMinCurrency.setOnItemClickListener { _, _, i, _ ->
                tilMinCurrency.isErrorEnabled = false
                currencyIds[2] = i + 1
                calculate()
            }

            actMaxCurrency.setOnItemClickListener { _, _, i, _ ->
                tilMaxCurrency.isErrorEnabled = false
                currencyIds[3] = i + 1
                calculate()
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

    private fun Double.roundingSum(): Double {
        val price = this
        val cost = binding.etCostPrice.text.toString().toDouble
        val sum = if (cost < 1) 100 else 500
        val divider = if (cost < 1) 100 else 1000
        return ((price + sum) / divider).format(0).toDouble() * divider
    }

    private val checkCurrencies: Boolean
        get() {
            for (e in currencyIds)
                if (e == -1)
                    return false
            return true
        }

    private fun calculate() {
        binding.apply {
            val price = etCostPrice.text.toString().toDouble

            val wholesale =
                (mapOfRates["${actCostCurrency.text}${actWholesaleCurrency.text}"] ?: 0.0) * price
            val min =
                (mapOfRates["${actCostCurrency.text}${actMinCurrency.text}"] ?: 0.0) * price
            val max =
                (mapOfRates["${actCostCurrency.text}${actMaxCurrency.text}"] ?: 0.0) * price

            val wholesalePrice = ((wholesalePercent / 100.0 + 1) * wholesale)
            val minPrice = ((minPercent / 100.0 + 1) * min)
            val maxPrice = ((maxPercent / 100.0 + 1) * max)

            etWholesalePrice.setText((wholesalePrice format 2).sumFormat)
            etMinPrice.setText((minPrice format 2).sumFormat)
            etMaxPrice.setText((maxPrice format 2).sumFormat)

            if (price == 0.0) {
                etWholesalePrice.text!!.clear()
                etMinPrice.text!!.clear()
                etMaxPrice.text!!.clear()
            }
        }
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
                etMinPrice.setText(minPrice.roundingSum() format 2)
                etMaxPrice.setText(maxPrice.roundingSum() format 2)

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
                        mapOfRates.clear()
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
                                mapOfRates["${currency.code}${currency.code}"] = 1.0
                                mapOfRates["${toCurrency.code}${toCurrency.code}"] = 1.0
                                mapOfRates["${currency.code}${toCurrency.code}"] =
                                    toCurrency.rate.toDouble()
                                mapOfRates["${toCurrency.code}${currency.code}"] =
                                    1 / toCurrency.rate.toDouble()
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

        viewModel.warehouseProducts.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data!!.forEach { warehouseItem ->
                        productsList.add(warehouseItem.product.name)
                        if (!productsMap.contains(warehouseItem.product.name)) productsMap[warehouseItem.product.name] =
                            warehouseItem
                    }
                    binding.apply {
                        if (etProductName.text.isEmpty()) {
                            productsList.clear()
                            val arrayAdapter = ArrayAdapter(
                                requireContext(),
                                R.layout.item_spinner,
                                productsList.toMutableList()
                            )
                            etProductName.setAdapter(arrayAdapter)
                            etProductName.dismissDropDown()
                        } else {
                            val arrayAdapter = ArrayAdapter(
                                requireContext(),
                                R.layout.item_spinner,
                                productsList.toMutableList()
                            )
                            etProductName.setAdapter(arrayAdapter)
                            etProductName.showDropDown()
                            if (productsList.size == 1 && etProductName.text.toString() == productsList.firstOrNull()) {
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
