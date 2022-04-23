package uz.texnopos.elektrolife.ui.newsale.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.newsale.Basket
import uz.texnopos.elektrolife.ui.newsale.dialog.AddPaymentDialog
import uz.texnopos.elektrolife.ui.newsale.dialog.EditBasketProductDialog

class OrderFragment : Fragment(R.layout.fragment_order) {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private lateinit var addPaymentDialog: AddPaymentDialog
    private lateinit var editBasketProduct: EditBasketProductDialog
    private val viewModelOrder: OrderViewModel by viewModel()
    private val adapter: OrderAdapter by inject()
    private val settings: Settings by inject()
    private val safeArgs: OrderFragmentArgs by navArgs()
    private var price = MutableLiveData<Double>()
    private var basketListener = MutableLiveData<List<Product>>()
    private val gson = Gson()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrderBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.basket)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            val myType = object : TypeToken<List<Product>>() {}.type
            val productList = gson.fromJson<List<Product>>(safeArgs.products, myType)

            recyclerView.adapter = adapter
            adapter.models = Basket.products.toMutableList()
            tvTotalPrice.text = context?.getString(R.string.total_sum_text, "0", settings.currency)
            val totalPrice =
                productList.sumOf { product -> product.salePrice * product.count }

            price.postValue(totalPrice)

            price.observe(viewLifecycleOwner) { sum ->
                tvTotalPrice.text =
                    context?.getString(R.string.total_sum_text, sum.toSumFormat, settings.currency)
            }
            basketListener.observe(viewLifecycleOwner) { orders ->
                if (orders.isEmpty()) navController.popBackStack()
            }

            adapter.onPlusCounterClickListener { product ->
                // TODO: Edit product
//                Basket.addProduct(product) {
//                    adapter.plusCount(product)
//                    val newPrice =
//                        Basket.products.sumOf { product -> product.salePrice * product.count as Int }
//                    price.postValue(newPrice)
//                }
            }

            adapter.onMinusCounterClickListener { product ->
                // TODO: Edit product
//                Basket.minusProduct(product) {
//                    adapter.minusCount(product)
//                    val newPrice =
//                        Basket.products.sumOf { product -> product.salePrice * product.count as Int }
//                    price.postValue(newPrice)
//                }
            }

            adapter.setOnEditClickListener { product, position ->
                editBasketProduct = EditBasketProductDialog(product)
                editBasketProduct.setOnItemAddedListener { quantity, salePrice ->
                    Basket.setProduct(product, quantity, salePrice.toDouble())
                    adapter.notifyItemChanged(position)
                }

                editBasketProduct.show(
                    requireActivity().supportFragmentManager,
                    editBasketProduct.tag
                )
            }

            adapter.onDeleteItemClickListener { product, position ->
                showWarning(getString(R.string.confirm_remove_uz))
                    .setOnPositiveButtonClickListener {
                        adapter.removeItem(product, position)
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
                addPaymentDialog = AddPaymentDialog(finalPrice)
                addPaymentDialog.show(requireActivity().supportFragmentManager, "")
                val orders: MutableList<OrderItem> = mutableListOf()
                Basket.products.forEachIndexed { index, product ->
                    orders.add(
                        index,
                        OrderItem(product.id, product.count, 1, product.salePrice)
                    )
                }
                addPaymentDialog.sendData { clientId, cash, card, debt, date, comment ->
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
                    Basket.clear()
                    binding.apply {
                        tvTotalPrice.text = ""
                        adapter.models.clear()
                        adapter.notifyDataSetChanged()
                    }

                    showSuccess(getString(R.string.order_successfully_done))
                        .setOnPositiveButtonClickListener {
                            addPaymentDialog.dismiss()
                            navController.popBackStack(R.id.mainFragment, false)
                        }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }
}
