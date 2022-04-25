package uz.texnopos.elektrolife.ui.sales.detail

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.data.model.sales.Order
import uz.texnopos.elektrolife.data.model.sales.OrderResponse
import uz.texnopos.elektrolife.databinding.ActionBarSortBinding
import uz.texnopos.elektrolife.databinding.FragmentSalesDetailBinding
import uz.texnopos.elektrolife.databinding.LayoutPrintingBinding
import uz.texnopos.elektrolife.settings.Settings

class SalesDetailFragment : Fragment(R.layout.fragment_sales_detail) {
    private lateinit var binding: FragmentSalesDetailBinding
    private lateinit var abBinding: ActionBarSortBinding
    private lateinit var navController: NavController
    private lateinit var printingView: View
    private val viewModel: SalesDetailViewModel by viewModel()
    private val settings: Settings by inject()
    private val orderReceiptAdapter: OrderReceiptAdapter by inject()

    private val adapter: SalesDetailAdapter by inject()
    private val safeArgs: SalesDetailFragmentArgs by navArgs()
    private var basketId: Int = -1
    private lateinit var orders: List<Order>
    private lateinit var basket: Basket
    private lateinit var orderResponse: OrderResponse

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSalesDetailBinding.bind(view)
        abBinding = ActionBarSortBinding.bind(view)
        navController = findNavController()

        basket = Gson().fromJson(safeArgs.basket, Basket::class.java)
        basketId = basket.id

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
            btnSort.setImageResource(R.drawable.ic_print)
            btnSort.onClick {
                printReceipt(printingView)
            }
        }

        binding.apply {
            printingView = binding.root.inflate(R.layout.layout_printing)

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
                    adapter.models = it.data!!.orders
                    orders = it.data.orders
                    orderResponse = it.data
                    orderReceiptAdapter.models = it.data.orders
                    prepareReceipt(printingView)
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun prepareReceipt(view: View) {
        val viewBinding = LayoutPrintingBinding.bind(view)

        viewBinding.apply {
            ivLogo.setImageResource(R.drawable.logo)
            tvSeller.text = "Продавец: ${basket.employee.name}"
            val createdDate = basket.createdAt.substring(0..9).changeDateFormat
            val createdTime = basket.createdAt.substring(11..18)
            tvDate.text = "Время: $createdDate $createdTime"
            recyclerView.adapter = orderReceiptAdapter
            tvTotal.text = getString(
                R.string.price_text,
                orders.sumOf { p -> p.count * p.price }.toSumFormat,
                settings.currency
            )
            tvCash.text = getString(
                R.string.price_text,
                orderResponse.amount.cash.toSumFormat,
                settings.currency
            )
            tvCard.text = getString(
                R.string.price_text,
                orderResponse.amount.card.toSumFormat,
                settings.currency
            )
            tvDebtTitle.text = "Долг (до ${basket.term?.changeDateFormat})"
            tvDebt.text = getString(
                R.string.price_text,
                orderResponse.amount.debt.toSumFormat,
                settings.currency
            )
            GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(basket.qrLink), ivQrCode)
        }
    }

    private fun printReceipt(view: View) {
        val createdTime = basket.createdAt.replace('.', '_')
            .replace(' ', '_')
            .replace(':', '_')
        pdfGenerator(view, "${basket.id}_$createdTime",
            { response ->
                response?.let {
//                    showMessage(it.path)
                    doPrint(it.path)
                }
            },
            { failureResponse ->
                showError(failureResponse?.errorMessage)
            }
        )
    }
}
