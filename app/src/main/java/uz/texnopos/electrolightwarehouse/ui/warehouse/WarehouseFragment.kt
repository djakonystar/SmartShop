package uz.texnopos.electrolightwarehouse.ui.warehouse

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.databinding.ActionBarSortBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentWarehouseBinding

class WarehouseFragment : Fragment(R.layout.fragment_warehouse) {
    private lateinit var binding: FragmentWarehouseBinding
    private lateinit var abBinding: ActionBarSortBinding
    private lateinit var navController: NavController
    private val adapter: WarehouseAdapter by inject()
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
            }
            recyclerView.adapter = adapter
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            tilSearch.isEnabled = !loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun optionsMenu(view: View) {
        val optionsMenu = PopupMenu(requireContext(), view)
        val menuInflater = optionsMenu.menuInflater
        menuInflater.inflate(R.menu.menu_warehouse_sort, optionsMenu.menu)
        optionsMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                // TODO: Do get requests
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
            return@setOnMenuItemClickListener true
        }
        optionsMenu.show()
    }
}
