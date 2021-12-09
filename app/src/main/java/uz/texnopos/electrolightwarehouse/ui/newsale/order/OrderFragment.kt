package uz.texnopos.electrolightwarehouse.ui.newsale.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private val delay: Long = 1000
    private var lastTextEdit: Long = 0
    private var searchValue: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private var clientId: Int = 0



    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderBinding.bind(view)
        binding.apply {
            val myType = object : TypeToken<List<Product>>() {}.type
            val list = gson.fromJson<List<Product>>(safeArgs.products, myType)
            rvOrder.adapter = adapter
            rvOrder.addItemDecoration(MarginItemDecoration(8.dp))
            adapter.models = Basket.mutableProducts
            var totalPrice = 0
            for (i in list.indices){
                totalPrice+=list[i].salePrice
            }
            price.postValue(totalPrice.toLong())
            price.observe(viewLifecycleOwner,{
                tvTotalPrice.text = "Summa : $it uzs"
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
                    val dialog = AddPaymentDialog()
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
                    Toast.makeText(requireContext(), "Klientni qoshing!", Toast.LENGTH_SHORT).show()
                }

            }
        }
        setupObservers()

        val inputFinishChecker = Runnable {
            if (System.currentTimeMillis() > (lastTextEdit + delay - 500)) {
                viewModelClient.searchClient(searchValue)
            }
        }
        binding.etSearchClient.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            clientName = parent.getItemAtPosition(position).toString()
            clientId = listIds.getValue(clientName)
        }
        binding.etSearchClient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handler.removeCallbacks(inputFinishChecker)
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length!! > 0) {
                    list.clear()
                    searchValue = p0.toString()
                    lastTextEdit = System.currentTimeMillis()
                    handler.postDelayed(inputFinishChecker, delay)
                }
            }

        })

    }

    private fun setupObservers(){
        viewModelNewClient.registerNewClient.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{

                }
                ResourceState.SUCCESS->{

                    Toast.makeText(requireContext(), "Klient qo'shildi", Toast.LENGTH_SHORT).show()
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
                    }
                    //TODO: AlertDialog
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
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