package uz.texnopos.electrolightwarehouse.ui.newsale.order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.MarginItemDecoration
import uz.texnopos.electrolightwarehouse.core.extensions.dp
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.databinding.FragmentOrderBinding
import uz.texnopos.electrolightwarehouse.ui.newsale.dialog.AddClientDialog
import com.google.gson.reflect.TypeToken




class OrderFragment:Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding
    private val adapter: OrderAdapter by inject()
    private val gson = Gson()
    private val safeArgs: OrderFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderBinding.bind(view)
        binding.apply {
            val myType = object : TypeToken<List<Product>>() {}.type
            val list = gson.fromJson<List<Product>>(safeArgs.products, myType)
            rvOrder.adapter = adapter
            rvOrder.addItemDecoration(MarginItemDecoration(8.dp))
            adapter.models = list
            var totalPrice = 0
            var eachPrice = 0
            for (i in list.indices){
                eachPrice+=list[i].salePrice*list[i].count
            }
            totalPrice+=eachPrice
            tvTotalPrice.text = "Total price : $totalPrice uzs"
            btnBack.onClick {
                findNavController().popBackStack()
            }
            btnAddClient.onClick {
                val dialog = AddClientDialog()
                dialog.show(requireActivity().supportFragmentManager,"")
                dialog.setDate { name, inn, phone, type ->

                }
            }

            btnOrder.onClick {

            }
        }
    }
}