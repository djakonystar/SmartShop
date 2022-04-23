package uz.texnopos.elektrolife.ui.newsale

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.data.model.category.CategoryResponse
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ActionBarSearchBinding
import uz.texnopos.elektrolife.databinding.FragmentNewSaleBinding
import uz.texnopos.elektrolife.ui.newsale.dialog.AddToBasketDialog

class NewSaleFragment : Fragment(R.layout.fragment_new_sale) {
    private lateinit var binding: FragmentNewSaleBinding
    private lateinit var abBinding: ActionBarSearchBinding
    private lateinit var navController: NavController
    private val viewModel: NewSaleViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val productNewSaleAdapter: NewSaleProductAdapter by inject()
    private var productsList = mutableListOf<Product>()
    private var allProductsList = mutableListOf<Product>()
    private var searchValue = ""
    private var selectedCategoryId = -1
    private var selectedChipId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewSaleBinding.bind(view)
        abBinding = ActionBarSearchBinding.bind(view)
        navController = findNavController()

        categoryViewModel.getCategories()
        viewModel.getProducts()
        setUpObservers()


        abBinding.apply {
            btnHome.onClick {
                navController.popBackStack()
                hideSoftKeyboard(btnHome)
            }

            etSearch.addTextChangedListener {
                searchValue = it.toString()
                viewModel.getProducts(searchValue)
            }

            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.getProducts(searchValue)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        binding.apply {
            recyclerView.adapter = productNewSaleAdapter

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                chipGroup.removeAllViews()
                categoryViewModel.getCategories()
                viewModel.getProducts(searchValue)
            }

            productNewSaleAdapter.onItemClickListener { product ->
                val dialog = AddToBasketDialog(product)
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
                dialog.setOnItemAddedListener { quantity, salePrice ->
                    Basket.setProduct(product, quantity, salePrice.toDouble())
                }
                dialog.setOnDismissListener {
                    hideSoftKeyboard(btnFab)
                }
            }

            chipGroup.check(selectedChipId)

            btnFab.onClick {
                if (Basket.products.isNotEmpty()) {
                    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                    val gsonString = gsonPretty.toJson(Basket.products)
                    selectedChipId = chipGroup.checkedChipId
                    findNavController().navigate(
                        NewSaleFragmentDirections.actionNewSaleFragmentToOrderFragment(
                            gsonString
                        )
                    )
                } else {
                    context?.getString(R.string.basket_empty_warning)?.let { text ->
                        Snackbar.make(btnFab, text, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            btnFab.isEnabled = !loading
        }
    }

    private fun showLottieAnimation(show: Boolean) {
        binding.apply {
            lottieAnimation.isVisible = show
            recyclerView.isVisible = !show
            lottieAnimation.playAnimation()
        }
    }

    private fun setUpObservers() {
        categoryViewModel.categories.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data!!.forEach { category ->
                        addNewChip(category)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        viewModel.products.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    allProductsList = it.data!! as MutableList<Product>
                    productsList = if (selectedCategoryId == -1) {
                        it.data as MutableList<Product>
                    } else {
                        it.data.filter { product ->
                            product.category.id == selectedCategoryId
                        } as MutableList<Product>
                    }
                    productNewSaleAdapter.models = productsList
                    showLottieAnimation(productsList.isEmpty())
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    private fun addNewChip(category: CategoryResponse) {
        try {
            binding.apply {
                val inflater = LayoutInflater.from(requireContext())

                val newChip =
                    inflater.inflate(R.layout.item_chip_choice, chipGroup, false) as Chip
                newChip.text = category.name

                chipGroup.addView(newChip)

                newChip.setOnCheckedChangeListener { buttonView, isChecked ->
                    selectedCategoryId = if (isChecked) {
                        chipGroup.check((buttonView as Chip).id)
                        category.id
                    } else {
                        -1
                    }

                    if (selectedCategoryId != -1) {
                        viewModel.getProducts(searchValue)
                    } else {
                        productNewSaleAdapter.models = allProductsList
                        showLottieAnimation(allProductsList.isEmpty())
                    }
                }
            }
        } catch (e: Exception) {
            showError(e.localizedMessage)
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
