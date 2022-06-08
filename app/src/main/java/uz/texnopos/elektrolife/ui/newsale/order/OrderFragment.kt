package uz.texnopos.elektrolife.ui.newsale.order

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.newsale.Order
import uz.texnopos.elektrolife.data.model.newsale.OrderItem
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentOrderBinding
import uz.texnopos.elektrolife.databinding.LayoutPrintingBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.dialog.SuccessOrderDialog
import uz.texnopos.elektrolife.ui.newsale.Basket
import uz.texnopos.elektrolife.ui.newsale.dialog.OrderCheckoutDialog
import uz.texnopos.elektrolife.ui.newsale.dialog.EditBasketProductDialog
import uz.texnopos.elektrolife.ui.sales.detail.OrderReceiptAdapter
import java.util.*

class OrderFragment : Fragment(R.layout.fragment_order) {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private lateinit var orderCheckoutDialog: OrderCheckoutDialog
    private lateinit var editBasketProduct: EditBasketProductDialog
    private lateinit var printingView: View
    private val viewModelOrder: OrderViewModel by viewModel()
    private val adapter: OrderAdapter by inject()
    private val settings: Settings by inject()
    private val orderReceiptAdapter: OrderReceiptAdapter by inject()
    private var price = MutableLiveData<Double>()
    private var basketListener = MutableLiveData<List<Product>>()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrderBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        printingView = binding.root.inflate(R.layout.layout_printing)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.basket)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            adapter.models = Basket.products.toMutableList()
            tvTotalPrice.text = context?.getString(R.string.total_sum_text, "0", settings.currency)
            val totalPrice =
                Basket.products.sumOf { product -> product.salePrice * product.count }

            price.postValue(totalPrice)

            price.observe(viewLifecycleOwner) { sum ->
                tvTotalPrice.text = context?.getString(
                    R.string.total_sum_text,
                    sum.format(2).sumFormat,
                    settings.currency
                )
            }
            basketListener.observe(viewLifecycleOwner) { orders ->
                if (orders.isEmpty()) navController.popBackStack()
            }

            adapter.setOnEditClickListener { product, position ->
                editBasketProduct = EditBasketProductDialog(product)
                editBasketProduct.setOnItemAddedListener { quantity, salePrice ->
                    Basket.setProduct(product, quantity, salePrice)
                    adapter.notifyItemChanged(position)
                    val total =
                        Basket.products.sumOf { product -> product.salePrice * product.count }

                    price.postValue(total)
                }

                editBasketProduct.show(
                    requireActivity().supportFragmentManager,
                    editBasketProduct.tag
                )
            }

            adapter.onDeleteItemClickListener { product, position ->
                showWarning(getString(R.string.confirm_remove_uz))
                    .setOnPositiveButtonClickListener {
                        adapter.removeItem(product, position) {
                            adapter.models = it
                        }
                        Basket.deleteProduct(product)
                        val newPrice = Basket.products.sumOf { product ->
                            product.salePrice * product.count
                        }
                        price.postValue(newPrice)
                        basketListener.postValue(Basket.products)
                    }
            }

            btnOrder.onClick {
                val finalPrice =
                    tvTotalPrice.text.filter { c -> c.isDigit() || c == '.' }.toString().toDouble()
                orderCheckoutDialog = OrderCheckoutDialog(finalPrice)
                orderCheckoutDialog.show(requireActivity().supportFragmentManager, "")
                val orders: MutableList<OrderItem> = mutableListOf()
                Basket.products.forEachIndexed { index, product ->
                    orders.add(
                        index,
                        OrderItem(product.id, product.count, 1, product.salePrice)
                    )
                }
                orderCheckoutDialog.sendData { clientId, cash, card, debt, date, comment ->
                    viewModelOrder.setOrder(
                        Order(
                            id = clientId,
                            card = card,
                            cash = cash,
                            debt = debt,
                            term = date,
                            description = comment,
                            orders = orders
                        )
                    )
                }
            }
        }

        setUpObservers()
    }

    override fun onDetach() {
        adapter.models = mutableListOf()
        super.onDetach()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            recyclerView.isEnabled = !loading
            btnOrder.isEnabled = !loading
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpObservers() {
        viewModelOrder.orderState.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    orderReceiptAdapter.models = it.data!!.orders
                    prepareReceipt(printingView, it.data)
                    val successDialog =
                        SuccessOrderDialog(getString(R.string.order_successfully_done))
                    successDialog.setOnPrintButtonClickListener {
                        printReceipt(printingView, it.data)
                    }
                    successDialog.setOnPositiveButtonClickListener {
                        binding.apply {
                            tvTotalPrice.text = ""
                            adapter.models = mutableListOf()
                        }
                        orderCheckoutDialog.dismiss()
                        Basket.clear()
                        navController.popBackStack(R.id.mainFragment, false)
                    }
                    successDialog.show(requireActivity().supportFragmentManager, successDialog.tag)
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun prepareReceipt(view: View, basket: orderBasketResponse) {
        val viewBinding = LayoutPrintingBinding.bind(view)
        val orders = basket.orders

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
            if (basket.amount.cash <= 0) {
                tvCashTitle.isVisible = false
                tvDotsCash.isVisible = false
                tvCash.isVisible = false
            }
            tvCash.text = getString(
                R.string.price_text,
                basket.amount.cash.toSumFormat,
                settings.currency
            )
            if (basket.amount.card <= 0) {
                tvCardTitle.isVisible = false
                tvDotsCard.isVisible = false
                tvCard.isVisible = false
            }
            tvCard.text = getString(
                R.string.price_text,
                basket.amount.card.toSumFormat,
                settings.currency
            )
            if (basket.amount.debt <= 0) {
                tvDebtTitle.isVisible = false
                tvDotsDebt.isVisible = false
                tvDebt.isVisible = false
            }
            tvDebtTitle.text = "Долг (до ${basket.term?.changeDateFormat})"
            tvDebt.text = getString(
                R.string.price_text,
                basket.amount.debt.toSumFormat,
                settings.currency
            )
            GlideToVectorYou.justLoadImage(requireActivity(), Uri.parse(basket.qrLink), ivQrCode)
        }
    }

    private fun printReceipt(view: View, basket: orderBasketResponse) {
        val createdTime = basket.createdAt.replace('.', '_')
            .replace(' ', '_')
            .replace(':', '_')
        val fileName = "${createdTime}_${basket.id}"
        pdfGenerator(view, fileName,
            { response ->
                response?.let {
                    doPrint(it.path, fileName)
                    binding.apply {
                        tvTotalPrice.text = ""
                        adapter.models = mutableListOf()
                    }
                    orderCheckoutDialog.dismiss()
                    Basket.clear()
                    navController.popBackStack(R.id.mainFragment, false)
                }
            },
            { failureResponse ->
                showError(failureResponse?.errorMessage)
            }
        )
    }
}
