package uz.texnopos.elektrolife.ui.newsale.order

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
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
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.newsale.Order
import uz.texnopos.elektrolife.data.model.newsale.OrderItem
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentOrderBinding
import uz.texnopos.elektrolife.ui.newsale.Basket
import uz.texnopos.elektrolife.ui.newsale.dialog.AddPaymentDialog

class OrderFragment : Fragment(R.layout.fragment_order) {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModelOrder: OrderViewModel by viewModel()
    private val adapter: OrderAdapter by inject()
    private val safeArgs: OrderFragmentArgs by navArgs()
    private var price = MutableLiveData<Long>()
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
            adapter.models = Basket.mutableProducts
            tvTotalPrice.text = context?.getString(R.string.total_sum_text, "0")
            val totalPrice = productList.sumOf { product -> product.salePrice * product.count }

            price.postValue(totalPrice)

            price.observe(viewLifecycleOwner) { sum ->
                tvTotalPrice.text = context?.getString(R.string.total_sum_text, sum.toSumFormat)
            }
            basketListener.observe(viewLifecycleOwner) { orders ->
                if (orders.isEmpty()) navController.popBackStack()
            }

            adapter.onPlusCounterClickListener { product ->
                Basket.addProduct(product) {
                    adapter.plusCount(product)
                    val newPrice =
                        Basket.mutableProducts.sumOf { product -> product.salePrice * product.count }
                    price.postValue(newPrice)
                }
            }

            adapter.onMinusCounterClickListener { product ->
                Basket.minusProduct(product) {
                    adapter.minusCount(product)
                    val newPrice =
                        Basket.mutableProducts.sumOf { product -> product.salePrice * product.count }
                    price.postValue(newPrice)
                }
            }

            adapter.onDeleteItemClickListener { product, position ->
                //todo custom dialog
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(context?.getString(R.string.remove_uz))
                    .setMessage(context?.getString(R.string.confirm_remove_uz))
                    .setPositiveButton(context?.getString(R.string.yes)) { _, _ ->
                        adapter.removeItem(product, position)
                        Basket.mutableProducts.remove(product)
                        val newPrice =
                            Basket.mutableProducts.sumOf { product -> product.salePrice * product.count }
                        price.postValue(newPrice)
                        basketListener.postValue(Basket.mutableProducts)
                    }
                dialog.show()
            }

            btnOrder.onClick {
                val finalPrice = tvTotalPrice.text.filter { c -> c.isDigit() }.toString().toLong()
                val dialog = AddPaymentDialog(finalPrice)
                dialog.show(requireActivity().supportFragmentManager, "")
                val orders: MutableList<OrderItem> = mutableListOf()
                Basket.products.forEachIndexed { index, product ->
                    orders.add(
                        index,
                        OrderItem(product.productId, product.count, product.salePrice)
                    )
                }
                dialog.setDate { clientId, cash, card, debt, date, comment ->
                    viewModelOrder.setOrder(
                        Order(
                            id = clientId,
                            card = card,
                            cash = cash,
                            debt = debt,
                            price = finalPrice,
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
                    Basket.mutableProducts.clear()
                    binding.apply {
                        tvTotalPrice.text = ""
                        adapter.models.clear()
                        adapter.notifyDataSetChanged()
                    }

                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle(context?.getString(R.string.success))
                    alertDialog.setMessage(context?.getString(R.string.order_successfully_done))
                    alertDialog.show()
                    basketListener.postValue(Basket.mutableProducts)
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle(context?.getString(R.string.error))
                    alertDialog.setMessage(it.message)
                    alertDialog.show()
                }
            }
        }
    }
}
