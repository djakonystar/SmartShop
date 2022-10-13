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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.Constants.ROLE_ADMIN
import uz.texnopos.elektrolife.core.extensions.Constants.ROLE_CEO
import uz.texnopos.elektrolife.core.extensions.animateDebtPrice
import uz.texnopos.elektrolife.core.extensions.newSaleProduct
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.data.model.category.CategoryResponse
import uz.texnopos.elektrolife.databinding.ActionBarSortBinding
import uz.texnopos.elektrolife.databinding.FragmentWarehouseBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.newsale.CategoryViewModel
import uz.texnopos.elektrolife.ui.newsale.NewSaleViewModel

class WarehouseFragment : Fragment(R.layout.fragment_warehouse) {
    private lateinit var binding: FragmentWarehouseBinding
    private lateinit var abBinding: ActionBarSortBinding
    private lateinit var navController: NavController
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val productViewModel: NewSaleViewModel by viewModel()
    private val adapter: WarehouseAdapter by inject()
    private val settings: Settings by inject()
    private var sortType = "byFewRemain"
    private var productsList = mutableListOf<newSaleProduct>()
    private var allProductsList = mutableListOf<newSaleProduct>()
    private var searchValue = ""
    private var selectedCategoryId = -1
    private var selectedChipId: Int = -1
    private var isLoading = false
    private var page = 1
    private var lastPage = 0

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
            btnSort.isVisible = false
            btnSort.onClick {
                optionsMenu(btnSort)
            }
        }

        binding.apply {
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!isLoading && adapter.models.isNotEmpty() && page < lastPage &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    ) {
                        page++
                        productViewModel.getProducts(page, selectedCategoryId, searchValue, -1)
                    }
                }
            })

            adapter.setOnItemClickListener {
                if (settings.role == ROLE_CEO || settings.role == ROLE_ADMIN) {
                    navController.navigate(
                        WarehouseFragmentDirections.actionWarehouseFragmentToEditProductFragment(it)
                    )
                }

            }

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                chipGroup.removeAllViews()
                productsList = mutableListOf()
                categoryViewModel.getCategories()
                page = 1
                adapter.models = listOf()
                productViewModel.getProducts(page, searchValue, -1)
            }

            etSearch.addTextChangedListener {
                searchValue = it.toString()
                searchValue.ifEmpty { page = 1 }
                adapter.models = listOf()
                productViewModel.getProducts(page, selectedCategoryId, searchValue, -1)
            }

            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    productViewModel.getProducts(page, selectedCategoryId, searchValue, -1)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        categoryViewModel.getCategories()
        productViewModel.getProducts(page, searchValue, -1)
        setUpObservers()
    }

    override fun onDetach() {
        adapter.models = listOf()
        super.onDetach()
    }

    private fun setUpObservers() {
        productViewModel.products.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    lastPage = it.data!!.lastPage
                    allProductsList = it.data.data as MutableList<newSaleProduct>
                    if (adapter.models.isEmpty()) {
                        adapter.models = allProductsList
                        productsList = allProductsList
                    } else {
                        allProductsList.forEach { product ->
                            if (!productsList.contains(product)) {
                                productsList.add(product)
                            }
                        }
                        adapter.models = productsList
                    }
                    showLottieAnimation(productsList.isEmpty())
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
//            viewModel.warehouseProducts(searchValue)
            return@setOnMenuItemClickListener true
        }
        optionsMenu.show()
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

                    page = 1
                    adapter.models = listOf()
                    productViewModel.getProducts(page, selectedCategoryId, searchValue, -1)

//                    if (selectedCategoryId != -1) {
//                        viewModel.warehouseProducts(searchValue)
//                    } else {
//                        adapter.models = allProductsList
////                        adapter.models = when (sortType) {
////                            "byFewRemain" -> allProductsList
////                                .sortedBy { t ->
////                                    t.remained / (t.category?.minCount?.toDouble() ?: 0.0)
////                                }
////                            "byProduct" -> allProductsList
////                                .sortedBy { t -> t.name.lowercase() }
////                            "byCategory" -> allProductsList
////                                .sortedBy { t -> t.name.lowercase() }
////                                .sortedBy { t -> t.category?.name?.lowercase() }
////                            "byRemainAscend" -> allProductsList
////                                .sortedBy { t -> t.remained }
////                            "byRemainDescend" -> allProductsList
////                                .sortedByDescending { t -> t.remained }
////                            else -> allProductsList
////                        }
//                        showLottieAnimation(allProductsList.isEmpty())
//                    }
                }
            }
        } catch (e: Exception) {
            showError(e.localizedMessage)
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
