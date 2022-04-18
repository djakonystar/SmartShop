package uz.texnopos.elektrolife.ui.sales.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.data.model.sales.Order
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentSalesDetailBinding
import uz.texnopos.elektrolife.settings.Settings

class SalesDetailFragment : Fragment(R.layout.fragment_sales_detail) {
    private lateinit var binding: FragmentSalesDetailBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: SalesDetailViewModel by viewModel()
    private val settings: Settings by inject()

    private val adapter: SalesDetailAdapter by inject()
    private val safeArgs: SalesDetailFragmentArgs by navArgs()
    private var basketId: Int = -1
    private lateinit var orders: List<Order>
    private lateinit var basket: Basket

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSalesDetailBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        basket = Gson().fromJson(safeArgs.basket, Basket::class.java)
        basketId = basket.id

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            val totalPrice = basket.cash + basket.card + basket.debt.debt

            animateTotalPrice(0.0, totalPrice, tvTotalPrice, settings)
            animateDebtPrice(0.0, basket.debt.debt, tvDebtPrice, settings)
            tvCashPrice.text = getString(
                R.string.price_text,
                basket.cash.checkModule.toSumFormat,
                settings.currency
            )
            tvCardPrice.text = getString(
                R.string.price_text,
                basket.card.checkModule.toSumFormat,
                settings.currency
            )

            tvCashPrice.isSelected = true
            tvCardPrice.isSelected = true
            tvDebtPrice.isSelected = true
            tvTotalPrice.isSelected = true

            etSearch.addTextChangedListener {
                adapter.models = orders.filter { product ->
                    product.productName.contains(it.toString(), true)
                }
            }
        }

        viewModel.getOrders(basketId)
        setUpObservers()
    }

    override fun onDetach() {
        super.onDetach()
        adapter.models = listOf()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            mainContainer.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.orders.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    adapter.models = it.data!!
                    orders = it.data
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
