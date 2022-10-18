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

                val orders: MutableList<OrderItem> = mutableListOf()
                Basket.products.forEachIndexed { index, product ->
                    orders.add(
                        index,
                        OrderItem(product.id, product.count, 1, product.salePrice)
                    )
                }
                orderCheckoutDialog = OrderCheckoutDialog(finalPrice, orders)
                orderCheckoutDialog.show(requireActivity().supportFragmentManager, "")

                orderCheckoutDialog.setOnDismissListener {
                    tvTotalPrice.text = ""
                    adapter.models = mutableListOf()
                    Basket.clear()
                    navController.popBackStack(R.id.mainFragment, false)
                }
            }
        }
    }

    override fun onDetach() {
        adapter.models = mutableListOf()
        super.onDetach()
    }
}
