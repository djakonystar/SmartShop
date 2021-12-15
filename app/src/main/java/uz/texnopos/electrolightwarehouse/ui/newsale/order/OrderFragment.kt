package uz.texnopos.electrolightwarehouse.ui.newsale.order

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.android.inject
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MarginItemDecoration
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.dp
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.core.extensions.showMessage
import uz.texnopos.electrolightwarehouse.data.model.Order
import uz.texnopos.electrolightwarehouse.data.model.OrderItem
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.data.newClient.RegisterClient
import uz.texnopos.electrolightwarehouse.databinding.FragmentOrderBinding
import uz.texnopos.electrolightwarehouse.ui.client.ClientsViewModel
import uz.texnopos.electrolightwarehouse.ui.newclient.NewClientViewModel
import uz.texnopos.electrolightwarehouse.ui.newsale.Basket
import uz.texnopos.electrolightwarehouse.ui.newsale.dialog.AddClientDialog
import uz.texnopos.electrolightwarehouse.ui.newsale.dialog.AddPaymentDialog


class OrderFragment:Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding
    private val adapter: OrderAdapter by inject()
    private val gson = Gson()
    private val safeArgs: OrderFragmentArgs by navArgs()
    private var price = MutableLiveData<Long>()
    private val viewModelOrder:OrderViewModel by inject()
    private val viewModelClient: ClientsViewModel by inject()
    private val viewModelNewClient : NewClientViewModel by inject()
    private var clientName = ""
    private var list: MutableSet<String> = mutableSetOf()
    private var listIds: MutableMap<String, Int> = mutableMapOf()
    private lateinit var adapterArray: ArrayAdapter<String>
    private var searchValue: String = ""
    private var clientId: Int = 0



    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderBinding.bind(view)
        binding.apply {
            val myType = object : TypeToken<List<Product>>() {}.type
            val list = gson.fromJson<List<Product>>(safeArgs.products, myType)
            rvOrder.adapter = adapter
            rvOrder.addItemDecoration(MarginItemDecoration(8.dp))
            adapter.models = Basket.mutableProducts
            tvTotalPrice
            var totalPrice = 0
            for (i in list.indices){
                totalPrice+=list[i].salePrice
            }
            price.postValue(totalPrice.toLong())
            price.observe(viewLifecycleOwner,{
                tvTotalPrice.text = "Summa : "+(it.changeFormat())
            })

            val orderItems : MutableList<OrderItem> = mutableListOf()
            list.forEachIndexed { index, product ->
                orderItems.add(index,
                    OrderItem(product.productId,product.count,product.salePrice.toLong())
                )
            }

            etSearchClient.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                clientName = parent.getItemAtPosition(position).toString()
            }

            btnBack.onClick {
                findNavController().popBackStack()
            }

            adapter.onItemClickListener {
                Basket.mutableProducts.remove(it)
            }

            btnAddClient.onClick {
                val dialog = AddClientDialog()
                dialog.show(requireActivity().supportFragmentManager,"")
                dialog.setDate { name, inn, phone, type ->
                    viewModelNewClient.registerNewClient(RegisterClient(name,phone,inn,"",type))
                }
            }

            btnOrder.onClick {
                if (etSearchClient.text.isNotEmpty()){
                    val dialog = AddPaymentDialog(totalPrice.toLong())
                    dialog.show(requireActivity().supportFragmentManager,"")
                    dialog.setDate { card, cash, debt, description, data ->
                        viewModelOrder.setOrder(
                            Order(
                                id = clientId,
                                card = card,
                                cash = cash,
                                debt = debt,
                                price= totalPrice.toDouble(),
                                term =data,
                                description = description,
                                orders = orderItems
                            )
                        )
                    }
                }else{
                    showMessage(context?.getString(R.string.choose_client))
                }

            }
        }
        setupObservers()

        binding.etSearchClient.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            clientName = parent.getItemAtPosition(position).toString()
            clientId = listIds.getValue(clientName)
        }
        binding.etSearchClient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length!! > 0) {
                    list.clear()
                    searchValue = p0.toString()
                    viewModelClient.searchClient(searchValue)
                }
            }

        })

    }

    private fun Long.changeFormat(): String {
        val num = this.toLong().toString()
        var s = ""
        val sz = num.length
        for (i in 0 until sz) {
            if (i != 0 && (i - sz % 3) % 3 == 0) s += ' '
            s += num[i]
        }
        return "$s UZS"
    }

    private fun setupObservers(){
        viewModelNewClient.registerNewClient.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{

                }
                ResourceState.SUCCESS->{
                    showMessage(context?.getString(R.string.client_successfully_added))
                }
                ResourceState.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModelOrder.orderState.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{
                }
                ResourceState.SUCCESS->{
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
                ResourceState.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModelClient.searchClient.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{

                }
                ResourceState.SUCCESS->{
                    if (it.data!!.successful){
                        it.data.payload.forEach {data->
                            list.add("${data.name}, ${data.phone}")
                            if (!listIds.contains("${data.name}, ${data.phone}")) listIds["${data.name}, ${data.phone}"]=data.clientId
                            adapterArray = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,list.toMutableList())
                            binding.etSearchClient.setAdapter(adapterArray)
                        }
                    }else{
                        Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()}
                }
                ResourceState.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()}
            }
        })
    }
}