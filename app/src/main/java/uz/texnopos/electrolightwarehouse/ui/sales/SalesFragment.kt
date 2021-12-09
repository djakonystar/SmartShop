package uz.texnopos.electrolightwarehouse.ui.sales

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentSalesBinding

class SalesFragment : Fragment(R.layout.fragment_sales) {
    private lateinit var binding: FragmentSalesBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val adapter: SalesAdapter by inject()
    private val viewModel: SalesViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getOrders()
        binding = FragmentSalesBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()
        abBinding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            swipeRefresh.setOnRefreshListener {
                viewModel.getOrders()
            }
        }

        adapter.onClickItem {
            val gsonPretty= GsonBuilder().setPrettyPrinting().create()
            val gsonString=gsonPretty.toJson(it)
            navController.navigate(SalesFragmentDirections.actionSalesFragmentToDetailSalesFragment(gsonString))
        }

        setUpObservers()
    }

//    private fun setLoading(loading: Boolean) {
//        binding.apply {
//            progressBar.isVisible = loading
//            swipeRefresh.isEnabled = !loading
//        }
//    }

    private fun setUpObservers() {
        viewModel.orders.observe(viewLifecycleOwner, {
            when (it.status) {
                ResourceState.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                ResourceState.SUCCESS -> {
                    adapter.models = it.data!!
                    binding.swipeRefresh.isRefreshing = false
                }
                ResourceState.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        })
    }
}