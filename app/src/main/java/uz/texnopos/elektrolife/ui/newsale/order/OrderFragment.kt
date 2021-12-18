package uz.texnopos.elektrolife.ui.newsale.order

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
import uz.texnopos.elektrolife.data.model.newclient.RegisterClient
import uz.texnopos.elektrolife.data.model.newsale.Order
import uz.texnopos.elektrolife.data.model.newsale.OrderItem
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentOrderBinding
import uz.texnopos.elektrolife.ui.client.ClientsViewModel
import uz.texnopos.elektrolife.ui.newclient.NewClientViewModel
import uz.texnopos.elektrolife.ui.newsale.Basket
import uz.texnopos.elektrolife.ui.newsale.dialog.AddClientDialog
import uz.texnopos.elektrolife.ui.newsale.dialog.AddPaymentDialog

class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private lateinit var adapterArray: ArrayAdapter<String>
    private val viewModelOrder: OrderViewModel by viewModel()
    private val viewModelClient: ClientsViewModel by viewModel()
    private val viewModelNewClient: NewClientViewModel by viewModel()
    private val adapter: OrderAdapter by inject()
    private val safeArgs: OrderFragmentArgs by navArgs()
    private var list: MutableSet<String> = mutableSetOf()
    private var listIds: MutableMap<String, Int> = mutableMapOf()
    private var price = MutableLiveData<Long>()
    private val gson = Gson()
    private var clientName = ""
    private var clientId: Int = 0

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
            val totalPrice = productList.sumOf { product -> product.salePrice }

            price.postValue(totalPrice)

            price.observe(viewLifecycleOwner) { sum ->
                tvTotalPrice.text = context?.getString(R.string.total_sum_text, sum.toSumFormat)
            }

            val orderItems: MutableList<OrderItem> = mutableListOf()
            productList.forEachIndexed { index, product ->
                orderItems.add(
                    index,
                    OrderItem(product.productId, product.count, product.salePrice)
                )
            }

            etSearchClient.addTextChangedListener {
                val searchValue = it.toString()
                if (searchValue.isNotEmpty()) {
                    list.clear()
                    viewModelClient.searchClient(searchValue)
                }
            }

            etSearchClient.onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    clientName = parent.getItemAtPosition(position).toString()
                    clientId = listIds.getValue(clientName)
                }

            adapter.onItemClickListener { product, position ->
                //todo custom dialog
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(context?.getString(R.string.remove_uz))
                    .setMessage(context?.getString(R.string.confirm_remove_uz))
                    .setPositiveButton("Ha") { _, _ ->
                        adapter.removeItem(product, position)
                        Basket.mutableProducts.remove(product)
                        val newPrice = Basket.mutableProducts.sumOf { it.salePrice }
                        price.postValue(newPrice)
                    }
                dialog.show()
            }

            btnAddClient.onClick {
                val dialog = AddClientDialog()
                dialog.show(requireActivity().supportFragmentManager, "")
                dialog.setData { name, inn, phone, type, comment ->
                    viewModelNewClient.registerNewClient(
                        RegisterClient(
                            name = name,
                            phone = phone,
                            inn = inn,
                            about = comment,
                            clientType = type
                        )
                    )
                }
            }

            btnOrder.onClick {
                if (etSearchClient.text.isEmpty()) {
                    clientId = 0
                }
                val dialog = AddPaymentDialog(totalPrice)
                dialog.show(requireActivity().supportFragmentManager, "")
                dialog.setDate { cash, card, debt, date, comment ->
                    viewModelOrder.setOrder(
                        Order(
                            id = clientId,
                            card = card,
                            cash = cash,
                            debt = debt,
                            price = totalPrice,
                            term = date,
                            description = comment,
                            orders = orderItems
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
            btnAddClient.isEnabled = !loading
            recyclerView.isEnabled = !loading
            btnOrder.isEnabled = !loading
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpObservers() {
        viewModelNewClient.registerNewClient.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    showMessage(context?.getString(R.string.client_successfully_added))
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }

        viewModelOrder.orderState.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    Basket.mutableProducts.clear()
                    binding.apply {
                        etSearchClient.text.clear()
                        tvTotalPrice.text = ""
                        adapter.models.clear()
                        adapter.notifyDataSetChanged()
                    }

                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle(context?.getString(R.string.success))
                    alertDialog.setMessage(context?.getString(R.string.order_successfully_done))
                    alertDialog.show()
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }

        viewModelClient.searchClient.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        it.data.payload.forEach { data ->
                            list.add("${data.name}, ${data.phone}")
                            if (!listIds.contains("${data.name}, ${data.phone}")) listIds["${data.name}, ${data.phone}"] =
                                data.clientId
                            adapterArray = ArrayAdapter(
                                requireContext(),
                                R.layout.item_spinner,
                                list.toMutableList()
                            )
                            binding.etSearchClient.setAdapter(adapterArray)
                        }
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }
    }
}
