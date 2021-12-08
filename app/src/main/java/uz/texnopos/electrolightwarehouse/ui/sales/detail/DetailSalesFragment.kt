package uz.texnopos.electrolightwarehouse.ui.sales.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Payment
import uz.texnopos.electrolightwarehouse.data.model.ProductInfo
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.databinding.FragmentDetailSalesBinding

class DetailSalesFragment: Fragment(R.layout.fragment_detail_sales) {

   private lateinit var binding:FragmentDetailSalesBinding
   private val adapter:DetailSalesAdapter by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailSalesBinding.bind(view)
        binding.apply {
            rvDetailSales.adapter = adapter

            btnBack.onClick {
                findNavController().popBackStack()
            }
        }
        adapter.models = listOf(
            Sales(1,"asd","+998 231545",
                listOf(Payment(1,100.0,0.0,0.0,100.0,"","",false,"21.02.2021г")),
                listOf(ProductInfo(1,1,"",200))
            )
        )
    }
}