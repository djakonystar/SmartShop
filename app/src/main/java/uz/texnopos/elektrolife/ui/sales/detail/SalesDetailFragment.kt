package uz.texnopos.elektrolife.ui.sales.detail

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.data.model.sales.Product
import uz.texnopos.elektrolife.data.model.sales.Sales
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentSalesDetailBinding

class SalesDetailFragment : Fragment(R.layout.fragment_sales_detail) {
    private lateinit var binding: FragmentSalesDetailBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController

    private val adapter: SalesDetailAdapter by inject()
    private val safeArgs: SalesDetailFragmentArgs by navArgs()
    private lateinit var orders: List<Product>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSalesDetailBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        val sales = Gson().fromJson(safeArgs.detail, Sales::class.java)
        orders = sales.orders

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            adapter.models = orders

            etSearch.addTextChangedListener {
                adapter.models = orders.filter { product ->
                    product.productName.contains(it.toString(), true)
                }
            }
        }
    }
}
