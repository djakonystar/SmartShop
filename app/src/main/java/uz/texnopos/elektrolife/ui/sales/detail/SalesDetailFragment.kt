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
import androidx.recyclerview.widget.RecyclerView
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
import uz.texnopos.elektrolife.databinding.FragmentSalesDetailBinding
import uz.texnopos.elektrolife.databinding.LayoutPrintingBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.dialog.ReturnOrderDialog
import uz.texnopos.elektrolife.ui.payment.AddPaymentDialog

class SalesDetailFragment : Fragment(R.layout.fragment_sales_detail) {
    private lateinit var binding: FragmentSalesDetailBinding
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
    private lateinit var returnOrderDialog: ReturnOrderDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSalesDetailBinding.bind(view)
        navController = findNavController()
        basket = Gson().fromJson(safeArgs.basket, Basket::class.java)

        basketId = basket.id

        binding.apply {
            printingView = binding.root.inflate(R.layout.layout_printing)

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
                viewModel.getOrders(basketId)
            }

            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && expandableFab.isVisible) expandableFab.hide()
                    else if (dy < 0 && !expandableFab.isVisible) expandableFab.show()
                }
            })

            adapter.setOnItemClickListener { position ->
                returnOrderDialog = ReturnOrderDialog(orderResponse, position)
                returnOrderDialog.setOnAddClickListener { viewModel.returnOrder(it) }
                returnOrderDialog.show(
                    requireActivity().supportFragmentManager,
                    returnOrderDialog.tag
                )
            }

            fabPrintReceipt.onClick {
                printReceipt(printingView)
            }

            fabRefundDebt.onClick {
                val addPaymentDialog = AddPaymentDialog(orderResponse)
                addPaymentDialog.setOnDismissListener {
                    viewModel.getOrders(basket.id)
                }
                addPaymentDialog.show(
                    requireActivity().supportFragmentManager,
                    addPaymentDialog.tag
                )
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
                    it.data?.let { order ->
                        adapter.models = order.orders
                        binding.etSearch.text?.clear()
                        orders = order.orders
                        orderResponse = order
                        setAmount()
                        orderReceiptAdapter.models = order.orders
                        prepareReceipt(printingView)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        viewModel.returningOrder.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showSuccess(getString(R.string.order_successfully_returned))
                        .setOnDismissListener {
                            returnOrderDialog.dismiss()
                            viewModel.getOrders(basketId)
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

    @SuppressLint("SetTextI18n")
    private fun prepareReceipt(view: View) {
        val viewBinding = LayoutPrintingBinding.bind(view)
        val order = orderResponse

        viewBinding.apply {
            ivLogo.setImageResource(R.drawable.logotype)
            val logoResId = resources.getIdentifier(
                Constants.provideBaseUrls()[settings.baseUrl] ?: "logo",
                "drawable",
                requireActivity().packageName
            )
            if (logoResId != 0) {
                ivLogo.setImageResource(logoResId)
            }
            tvCompanyName.text = settings.companyName
            tvCompanyAddress.text = settings.companyAddress
            tvCompanyPhone.text = "+998 ${settings.companyPhone.toPhoneNumber}"
            tvSeller.text = "Продавец: ${basket.employee.name}"
            val createdDate = basket.createdAt.substring(0..9).changeDateFormat
            val createdTime = basket.createdAt.substring(11..18)
            tvDate.text = "Время: $createdDate $createdTime"
            recyclerView.adapter = orderReceiptAdapter
            tvTotal.text = getString(
                R.string.price_text,
                order.amount.sum.toSumFormat,
                settings.currency
            )
            if (order.amount.cash <= 0) {
                tvCashTitle.isVisible = false
                tvDotsCash.isVisible = false
                tvCash.isVisible = false
            }
            tvCash.text = getString(
                R.string.price_text,
                order.amount.cash.toSumFormat,
                settings.currency
            )
            if (order.amount.card <= 0) {
                tvCardTitle.isVisible = false
                tvDotsCard.isVisible = false
                tvCard.isVisible = false
            }
            tvCard.text = getString(
                R.string.price_text,
                order.amount.card.toSumFormat,
                settings.currency
            )
            if (order.amount.debt <= 0) {
                tvDebtTitle.isVisible = false
                tvDotsDebt.isVisible = false
                tvDebt.isVisible = false
            }
            tvDebtTitle.text = "Долг (до ${basket.term?.changeDateFormat})"
            tvDebt.text = getString(
                R.string.price_text,
                order.amount.debt.toSumFormat,
                settings.currency
            )
            if (order.amount.paidDebt > 0.0) {
                tvDebtPaidTitle.isVisible = true
                tvDotsDebtPaid.isVisible = true
                tvDebtPaid.isVisible = true
                tvDebtPaid.text = getString(
                    R.string.price_text,
                    order.amount.paidDebt.toSumFormat,
                    settings.currency
                )
            }
            if (order.amount.remaining > 0.0) {
                tvDebtRemainedTitle.isVisible = true
                tvDotsDebtRemained.isVisible = true
                tvDebtRemained.isVisible = true
                tvDebtRemained.text = getString(
                    R.string.price_text,
                    order.amount.remaining.toSumFormat,
                    settings.currency
                )
            }
            GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(basket.qrLink), ivQrCode)
        }
    }

    private fun printReceipt(view: View) {
        val createdTime = basket.createdAt.replace('.', '_')
            .replace(' ', '_')
            .replace(':', '_')
        val fileName = "${createdTime}_${basket.id}"
        pdfGenerator(view, fileName,
            { response ->
                response?.let {
//                    showMessage(it.path)
                    doPrint(it.path, fileName)
                }
            },
            { failureResponse ->
                showError(failureResponse?.errorMessage)
            }
        )
    }
}
