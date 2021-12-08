package uz.texnopos.electrolightwarehouse.ui.sales

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.data.model.Payment
import uz.texnopos.electrolightwarehouse.data.model.ProductInfo
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.databinding.FragmentSalesBinding

class SalesFragment: Fragment(R.layout.fragment_sales) {

    private val adapter:SalesAdapter by inject()
    private lateinit var binding:FragmentSalesBinding
    private val viewModel : SalesViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSalesBinding.bind(view)
        setObservers()
        binding.apply {
            rvSales.adapter = adapter
        }
        binding.btnBack.onClick {
            findNavController().popBackStack()
        }

        adapter.models = listOf(
            Sales(1,"asd","+998 231545",
                listOf(Payment(1,100.0,0.0,0.0,100.0,"","",false,"21.02.2021г")),
                listOf(ProductInfo(1,1,"",200))
            )
        )

        adapter.onClickItem {
            findNavController().navigate(SalesFragmentDirections.actionSalesFragmentToDetailSalesFragment())
        }
    }

    private fun setObservers(){
        viewModel.orders.observe(viewLifecycleOwner,{
            when(it.status){
                ResourceState.LOADING->{

                }
                ResourceState.SUCCESS->{

                }
                ResourceState.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}