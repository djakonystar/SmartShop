package uz.texnopos.electrolightwarehouse.ui.warehouse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.showMessage
import uz.texnopos.electrolightwarehouse.data.model.warehouse.WarehouseProduct
import uz.texnopos.electrolightwarehouse.databinding.ActionBarSortBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentWarehouseBinding
import java.util.*
import kotlin.collections.ArrayList

class WarehouseFragment : Fragment(R.layout.fragment_warehouse) {
    private lateinit var binding: FragmentWarehouseBinding
    private lateinit var abBinding: ActionBarSortBinding
    private lateinit var navController: NavController
    private val viewModel: WarehouseViewModel by viewModel()
    private val adapter: WarehouseAdapter by inject()
    private var productsList = listOf<WarehouseProduct>()
    private var sortType = "byFewRemain"

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
            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                viewModel.getProductsFromWarehouse()
            }
            recyclerView.adapter = adapter
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                p0?.let {
                    if (it.isEmpty()) {
                        adapter.models = productsList
                    } else {
                        filter(it.toString())
                    }
                }
            }

        })

        viewModel.getProductsFromWarehouse()
        setUpObservers()
    }

    private fun filter(text: String) {
        val filteredListName: ArrayList<WarehouseProduct> = ArrayList()
        for (eachName in productsList) {
            if (eachName.name.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                filteredListName.add(eachName)
            }
        }
        adapter.filterList(filteredListName)
    }

    private fun setUpObservers() {
        viewModel.warehouseProducts.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        val products = it.data.payload
                        productsList = when (sortType) {
                            "byFewRemain" -> products.sortedBy { t -> t.remained / t.category.minCount.toDouble() }
                            "byProduct" -> products.sortedBy { t -> t.name.lowercase() }
                            "byCategory" -> products.sortedBy { t -> t.category.name.lowercase() }
                            "byRemainAscend" -> products.sortedBy { t -> t.remained }
                            "byRemainDescend" -> products.sortedByDescending { t -> t.remained }
                            else -> products
                        }
                        adapter.models = productsList
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
            viewModel.getProductsFromWarehouse()
            return@setOnMenuItemClickListener true
        }
        optionsMenu.show()
    }
}
