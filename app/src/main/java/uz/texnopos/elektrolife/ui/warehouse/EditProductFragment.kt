package uz.texnopos.elektrolife.ui.warehouse

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.shaon2016.propicker.pro_image_picker.ProPicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import site.texnopos.djakonystar.suminputmask.SumInputMask
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.core.extensions.Constants.CLOUD_NAME
import uz.texnopos.elektrolife.data.model.category.CategoryResponse
import uz.texnopos.elektrolife.data.model.newproduct.Price
import uz.texnopos.elektrolife.data.model.warehouse.EditProduct
import uz.texnopos.elektrolife.databinding.ActionBarProductNewBinding
import uz.texnopos.elektrolife.databinding.FragmentProductEditBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.currency.CurrencyViewModel
import uz.texnopos.elektrolife.ui.newproduct.ImageViewModel
import uz.texnopos.elektrolife.ui.newsale.CategoryViewModel
import java.io.File

class EditProductFragment : Fragment(R.layout.fragment_product_edit) {
    private lateinit var binding: FragmentProductEditBinding
    private lateinit var abBinding: ActionBarProductNewBinding
    private lateinit var navController: NavController
    private lateinit var product: newSaleProduct
    private lateinit var categoriesAdapter: ArrayAdapter<String>
    private lateinit var imageUrl: String
    private val editProductViewModel: EditProductViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val currencyViewModel: CurrencyViewModel by viewModel()
    private val imageViewModel: ImageViewModel by viewModel()
    private val settings: Settings by inject()
    private val args: EditProductFragmentArgs by navArgs()
    private var categoriesList: MutableList<CategoryResponse> = mutableListOf()
    private var categoryName: MutableList<String> = mutableListOf()
    private var categoryId = -1
    private var liveCostPrice = MutableLiveData<Double>()
    private var wholesalePercent: Double = 0.0
    private var minPercent: Double = 0.0
    private var maxPercent: Double = 0.0
    private var mapOfCurrency: MutableMap<Int, String> = mutableMapOf()
    private var currencyIds = mutableListOf(-1, -1, -1, -1)
    private var mapOfRates = mutableMapOf<String, Double>()
    private var imagePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", "")
    private var presetPart: MultipartBody.Part =
        MultipartBody.Part.createFormData("upload_preset", "smart-shop")
    private var imageSelected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProductEditBinding.bind(view)
        abBinding = ActionBarProductNewBinding.bind(view)
        navController = findNavController()
        product = args.product

        categoryId = product.category.id
        wholesalePercent = product.category.wholePercent
        minPercent = product.category.minPercent
        maxPercent = product.category.maxPercent
        imageUrl = product.image ?: ""

        abBinding.apply {
            tvTitle.text = getString(R.string.edit_product)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            etProductName.setText(product.name)
            etBrand.setText(product.brand)

            product.image?.let { url ->
                Glide.with(this@EditProductFragment)
                    .load(url)
                    .into(ivProduct)
            }

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                currencyViewModel.getCurrency()
                categoryViewModel.getCategories()
            }

            setThreshold(
                actCategory,
                actCostCurrency,
                actWholesaleCurrency,
                actMinCurrency,
                actMaxCurrency
            )

            setCurrencyCalculator(
                acts = arrayOf(
                    actCostCurrency,
                    actWholesaleCurrency,
                    actMinCurrency,
                    actMaxCurrency
                ),
                tils = arrayOf(
                    tilCostCurrency,
                    tilWholesaleCurrency,
                    tilMinCurrency,
                    tilMaxCurrency
                )
            )

            SumInputMask(etCostPrice, etWholesalePrice, etMinPrice, etMaxPrice)

            etProductName.addTextChangedListener {
                tilProductName.isErrorEnabled = false
            }

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

            actCategory.setOnItemClickListener { _, _, i, _ ->
                categoryId = categoriesList[i].id
                wholesalePercent = categoriesList[i].wholePercent
                minPercent = categoriesList[i].minPercent
                maxPercent = categoriesList[i].maxPercent
            }

            cvImage.onClick {
                ProPicker.with(this@EditProductFragment)
                    .cropSquare()
                    .compressImage()
                    .maxResultSize(720, 720)
                    .start { resultCode, data ->
                        if (resultCode == Activity.RESULT_OK && data != null) {
                            val picker = ProPicker.getPickerData(data)
                            val fileUri = picker?.uri
                            binding.ivProduct.setImageURI(fileUri)

                            val file = File(fileUri?.path!!)
                            val image = file.asRequestBody("image/*".toMediaTypeOrNull())

                            imagePart =
                                MultipartBody.Part.createFormData("file", file.name, image)
                            imageSelected = true
                        }
                    }
            }

            btnSaveProduct.onClick {
                val productName = etProductName.text.toString()
                val brandName = etBrand.text.toString()
                val costPrice = etCostPrice.text.toString().toDouble
                val wholesalePrice = etWholesalePrice.text.toString().toDouble
                val minPrice = etMinPrice.text.toString().toDouble
                val maxPrice = etMaxPrice.text.toString().toDouble

                if (productName.isNotEmpty() && costPrice != 0.0 && wholesalePrice != 0.0 &&
                    minPrice != 0.0 && maxPrice != 0.0) {
                    if (imageSelected) {
                        imageViewModel.uploadImage(CLOUD_NAME, imagePart, presetPart)

                        imageViewModel.image.observe(viewLifecycleOwner) {
                            when (it.status) {
                                ResourceState.LOADING -> setLoading(true)
                                ResourceState.SUCCESS -> {
                                    setLoading(false)
                                    imageUrl = it.data!!.secureUrl
                                    editProductViewModel.editProduct(
                                        EditProduct(
                                            productId = product.id,
                                            categoryId = categoryId,
                                            name = productName,
                                            brand = brandName,
                                            costPrice = Price(currencyIds[0], costPrice),
                                            wholesalePrice = Price(currencyIds[1], wholesalePrice),
                                            minPrice = Price(currencyIds[2], minPrice),
                                            maxPrice = Price(currencyIds[3], maxPrice),
                                            image = imageUrl
                                        )
                                    )
                                }
                                ResourceState.ERROR -> {
                                    setLoading(false)
                                    showError(it.message)
                                }
                            }
                        }
                    } else {
                        editProductViewModel.editProduct(
                            EditProduct(
                                productId = product.id,
                                categoryId = categoryId,
                                name = productName,
                                brand = brandName,
                                costPrice = Price(currencyIds[0], costPrice),
                                wholesalePrice = Price(currencyIds[1], wholesalePrice),
                                minPrice = Price(currencyIds[2], minPrice),
                                maxPrice = Price(currencyIds[3], maxPrice),
                                image = imageUrl
                            )
                        )
                    }

                    editProductViewModel.editProduct.observe(viewLifecycleOwner) {
                        when (it.status) {
                            ResourceState.LOADING -> setLoading(true)
                            ResourceState.SUCCESS -> {
                                setLoading(false)
                                showSuccess(getString(R.string.product_edited_successfully))
                                    .setOnDismissListener {
                                        navController.popBackStack()
                                    }
                            }
                            ResourceState.ERROR -> {
                                setLoading(false)
                                showError(it.message)
                            }

                        }
                    }
                } else {
                    if (productName.isEmpty()) {
                        tilProductName.error = context?.getString(R.string.required_field)
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
                }
            }
        }

        categoryViewModel.getCategories()
        currencyViewModel.getCurrency()
        setUpObservers()
    }

    private fun setUpObservers() {
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

        categoryViewModel.categories.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    categoriesList = it.data!!.toMutableList()
                    categoriesList.forEach { category ->
                        if (!categoryName.contains(category.name)) categoryName.add(category.name)
                        if (category.id == categoryId) {
                            binding.actCategory.setText(category.name)
                        }
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

                            currencyIds[0] = product.costPrice.currencyId
                            currencyIds[1] = product.wholesalePrice.currencyId
                            currencyIds[2] = product.minPrice.currencyId
                            currencyIds[3] = product.maxPrice.currencyId

                            currency.rate.forEach { toCurrency ->
                                mapOfRates["${currency.code}${currency.code}"] = 1.0
                                mapOfRates["${toCurrency.code}${toCurrency.code}"] = 1.0
                                mapOfRates["${currency.code}${toCurrency.code}"] =
                                    toCurrency.rate.toDouble()
                                mapOfRates["${toCurrency.code}${currency.code}"] =
                                    1 / toCurrency.rate.toDouble()
                            }

                            when (currency.code) {
                                "USD" -> currency.rate.forEach { rate ->
                                    when (rate.code) {
                                        "UZS" -> settings.usdToUzs = rate.rate
                                    }
                                }
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

                        abBinding.tvRate.text = getString(
                            R.string.dollar_rate_text,
                            settings.usdToUzs.toString()
                        )

                        etCostPrice.setText(product.costPrice.price.toSumFormat)
                        etWholesalePrice.setText(product.wholesalePrice.price.toSumFormat)
                        etMinPrice.setText(product.minPrice.price.toSumFormat)
                        etMaxPrice.setText(product.maxPrice.price.toSumFormat)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    private fun setThreshold(vararg acts: AppCompatAutoCompleteTextView, value: Int = 100) {
        acts.forEach { act ->
            act.threshold = value
        }
    }

    private fun setCurrencyCalculator(
        vararg acts: AppCompatAutoCompleteTextView,
        tils: Array<TextInputLayout>
    ) {
        acts.forEachIndexed { index, act ->
            act.setOnItemClickListener { _, _, i, _ ->
                tils[index].isErrorEnabled = false
                if (currencyIds[index] != i + 1) {
                    currencyIds[index] = i + 1
                    calculate()
                }
            }
        }
    }

    private fun Double.roundingSum(): Double {
        val price = this
        val cost = binding.etCostPrice.text.toString().toDouble
        val sum = if (cost < 1) 100 else 500
        val divider = if (cost < 1) 100 else 1000
        return ((price + sum) / divider).format(0).toDouble() * divider
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

            etWholesalePrice.text?.clear()
            etWholesalePrice.setText((wholesalePrice format 2).sumFormat)
            etMinPrice.text?.clear()
            etMinPrice.setText((minPrice format 2).sumFormat)
            etMaxPrice.text?.clear()
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
}
