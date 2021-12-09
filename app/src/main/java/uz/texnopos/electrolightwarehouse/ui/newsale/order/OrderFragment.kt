package uz.texnopos.electrolightwarehouse.ui.newsale.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MarginItemDecoration
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.dp
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.FragmentOrderBinding
import uz.texnopos.electrolightwarehouse.ui.newsale.Basket
import uz.texnopos.electrolightwarehouse.ui.newsale.NewSaleFragment
import uz.texnopos.electrolightwarehouse.ui.newsale.dialog.AddClientDialog


class OrderFragment:Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding
    private val adapter: OrderAdapter by inject()
    private val gson = Gson()
    private val safeArgs: OrderFragmentArgs by navArgs()
    private var price = MutableLiveData<Long>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderBinding.bind(view)
        binding.apply {
            val myType = object : TypeToken<List<Product>>() {}.type
            val list = gson.fromJson<List<Product>>(safeArgs.products, myType)
            rvOrder.adapter = adapter
            rvOrder.addItemDecoration(MarginItemDecoration(8.dp))
            setupObservers()
            adapter.models = Basket.mutableProducts
            var totalPrice = 0
            for (i in list.indices){
                totalPrice+=list[i].salePrice
            }

            price.postValue(totalPrice.toLong())
            price.observe(viewLifecycleOwner,{
                tvTotalPrice.text = "Total price : $it uzs"
            })

            btnBack.onClick {
                findNavController().popBackStack()
            }

            adapter.onItemClickListener {
                Basket.mutableProducts.remove(it)
            }

            btnAddClient.onClick {
                val dialog = AddClientDialog()
                dialog.show(requireActivity().supportFragmentManager,"")
            }

            btnOrder.onClick {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Basket.mutableProducts.clear()
    }

    private fun setupObservers(){

    }
}