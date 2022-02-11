package uz.texnopos.elektrolife.ui.warehouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.data.model.newsale.CatalogCategory
import uz.texnopos.elektrolife.data.model.warehouse.Product
import uz.texnopos.elektrolife.databinding.ActionBarSortBinding
import uz.texnopos.elektrolife.databinding.FragmentWarehouseBinding
import uz.texnopos.elektrolife.ui.newsale.CategoriesViewModel
import java.util.*

class WarehouseFragment : Fragment(R.layout.fragment_warehouse) {
    private lateinit var binding: FragmentWarehouseBinding
    private lateinit var abBinding: ActionBarSortBinding
    private lateinit var navController: NavController
    private val viewModel: WarehouseViewModel by viewModel()
    private val categoryViewModel: CategoriesViewModel by viewModel()
    private val adapter: WarehouseAdapter by inject()
    private var sortType = "byFewRemain"
    private var productsList = mutableListOf<Product>()
    private var allProductsList = mutableListOf<Product>()
    private var searchValue = ""
    private var selectedCategoryId = -1
    private var selectedChipId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWarehouseBinding.bind(view)
        abBinding = ActionBarSortBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.warehouse)
            btnHome.onClick {
                navController.popBackStack()
            }
            btnSort.onClick {
                optionsMenu(btnSort)
            }
        }

        binding.apply {
            recyclerView.adapter = adapter

            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                chipGroup.removeAllViews()
                categoryViewModel.getCategories()
                viewModel.getProductsByName(searchValue)
            }

            etSearch.addTextChangedListener {
                searchValue = it.toString()
                viewModel.getProductsByName(searchValue)
            }

            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.getProductsByName(searchValue)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        categoryViewModel.getCategories()
        viewModel.getProductsByName(searchValue)
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.warehouseProducts.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        allProductsList = it.data.payload as MutableList<Product>
                        productsList = if (selectedCategoryId == -1) {
                            it.data.payload as MutableList<Product>
                        } else {
                            it.data.payload.filter { product ->
                                product.category.id == selectedCategoryId
                            } as MutableList<Product>
                        }
//                        adapter.models = it.data.payload
                        adapter.models = when (sortType) {
                            "byFewRemain" -> productsList
                                .sortedBy { t -> t.remained / t.category.minCount.toDouble() }
                            "byProduct" -> productsList
                                .sortedBy { t -> t.name.lowercase() }
                            "byCategory" -> productsList
                                .sortedBy { t -> t.name.lowercase() }
                                .sortedBy { t -> t.category.name?.lowercase() }
                            "byRemainAscend" -> productsList
                                .sortedBy { t -> t.remained }
                            "byRemainDescend" -> productsList
                                .sortedByDescending { t -> t.remained }
                            else -> productsList
                        }
                        showLottieAnimation(productsList.isEmpty())
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }

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
                    showMessage(it.message)
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun optionsMenu(view: View) {
        val optionsMenu = PopupMenu(requireContext(), view)
        val menuInflater = optionsMenu.menuInflater
        menuInflater.inflate(R.menu.menu_warehouse_sort, optionsMenu.menu)
        optionsMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sortProduct -> {
                    sortType = "byProduct"
                }
                R.id.sortCategory -> {
                    sortType = "byCategory"
                }
                R.id.sortAscend -> {
                    sortType = "byRemainAscend"
                }
                R.id.sortDescend -> {
                    sortType = "byRemainDescend"
                }
                R.id.sortFewRemain -> {
                    sortType = "byFewRemain"
                }
            }
            viewModel.getProductsByName(searchValue)
            return@setOnMenuItemClickListener true
        }
        optionsMenu.show()
    }

    private fun addNewChip(category: CatalogCategory) {
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
                        viewModel.getProductsByName(searchValue)
                    } else {
                        adapter.models = when (sortType) {
                            "byFewRemain" -> allProductsList
                                .sortedBy { t -> t.remained / t.category.minCount.toDouble() }
                            "byProduct" -> allProductsList
                                .sortedBy { t -> t.name.lowercase() }
                            "byCategory" -> allProductsList
                                .sortedBy { t -> t.name.lowercase() }
                                .sortedBy { t -> t.category.name?.lowercase() }
                            "byRemainAscend" -> allProductsList
                                .sortedBy { t -> t.remained }
                            "byRemainDescend" -> allProductsList
                                .sortedByDescending { t -> t.remained }
                            else -> allProductsList
                        }
                        showLottieAnimation(allProductsList.isEmpty())
                    }
                }
            }
        } catch (e: Exception) {
            showMessage(e.localizedMessage)
        }
    }

    private fun showLottieAnimation(show: Boolean) {
        binding.apply {
            lottieAnimation.isVisible = show
            recyclerView.isVisible = !show
            lottieAnimation.playAnimation()
        }
    }
}
