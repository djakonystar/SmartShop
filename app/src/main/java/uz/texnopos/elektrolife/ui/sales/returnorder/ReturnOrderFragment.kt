package uz.texnopos.elektrolife.ui.sales.returnorder

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
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.showSuccess
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.sales.Order
import uz.texnopos.elektrolife.data.model.sales.OrderResponse
import uz.texnopos.elektrolife.databinding.FragmentOrderReturnBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.dialog.ReturnOrderDialog
import uz.texnopos.elektrolife.ui.sales.detail.SalesDetailAdapter
import uz.texnopos.elektrolife.ui.sales.detail.SalesDetailViewModel

class ReturnOrderFragment : Fragment(R.layout.fragment_order_return) {
    private lateinit var binding: FragmentOrderReturnBinding
    private lateinit var navController: NavController
    private val salesDetailViewModel: SalesDetailViewModel by viewModel()
    private val settings: Settings by inject()
    private val adapter: SalesDetailAdapter by inject()
    private val safeArgs: ReturnOrderFragmentArgs by navArgs()
    private var basketId = -1
    private lateinit var orders: List<Order>
    private lateinit var orderResponse: OrderResponse
    private lateinit var returnOrderDialog: ReturnOrderDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrderReturnBinding.bind(view)
        navController = findNavController()
        orderResponse = Gson().fromJson(safeArgs.orderResponse, OrderResponse::class.java)
        basketId = orderResponse.id
        orders = orderResponse.orders

        binding.apply {
            btnHome.onClick {
                navController.popBackStack()
            }

            etSearch.addTextChangedListener {
                adapter.models = orders.filter { product ->
                    product.productName.contains(it.toString(), true)
                }
            }

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                etSearch.text!!.clear()
                salesDetailViewModel.getOrders(basketId)
            }

            adapter.models = orders
            recyclerView.adapter = adapter

            adapter.setOnItemClickListener { position ->
                returnOrderDialog = ReturnOrderDialog(orderResponse, position)
                returnOrderDialog.setOnAddClickListener { salesDetailViewModel.returnOrder(it) }
                returnOrderDialog.show(
                    requireActivity().supportFragmentManager,
                    returnOrderDialog.tag
                )
            }
        }

        setAmount()
        setUpObservers()
    }

    override fun onDetach() {
        adapter.models = listOf()
        super.onDetach()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            mainContainer.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        salesDetailViewModel.orders.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    it.data?.let { order ->
                        orderResponse = order
                        orders = orderResponse.orders
                        adapter.models = orders
                        binding.etSearch.text?.clear()
                        setAmount()
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        salesDetailViewModel.returningOrder.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(getString(R.string.order_successfully_returned))
                        .setOnDismissListener {
                            returnOrderDialog.dismiss()
                            salesDetailViewModel.getOrders(basketId)
                        }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    private fun setAmount() {
        val amount = orderResponse.amount
        binding.apply {
            tvTotalPrice.text = getString(
                R.string.total_price_text,
                amount.sum.toSumFormat,
                settings.currency
            )
            tvCashPrice.text = getString(
                R.string.cash_price_text,
                amount.cash.toSumFormat,
                settings.currency
            )
            tvCardPrice.text = getString(
                R.string.card_price_text,
                amount.card.toSumFormat,
                settings.currency
            )
            tvDebtPrice.text = getString(
                R.string.debt_price_text,
                amount.debt.toSumFormat,
                settings.currency
            )
            tvDebtPaidPrice.text = getString(
                R.string.debt_paid_price_text,
                amount.paidDebt.toSumFormat,
                settings.currency
            )
            tvDebtRemainedPrice.text = getString(
                R.string.debt_remained_price_text,
                amount.remaining.toSumFormat,
                settings.currency
            )
        }
    }
}
