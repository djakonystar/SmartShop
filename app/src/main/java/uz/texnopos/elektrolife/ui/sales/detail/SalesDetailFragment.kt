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
import uz.texnopos.elektrolife.core.extensions.animateDebtPrice
import uz.texnopos.elektrolife.core.extensions.animateTotalPrice
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.toSumFormat
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

        val basket = sales.basket

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            adapter.models = orders

            tvCashPrice.isSelected = true
            tvCardPrice.isSelected = true
            tvDebtPrice.isSelected = true
            tvTotalPrice.isSelected = true

            //tvTotalPrice.text = getString(R.string.total_sum_text, basket.price.toSumFormat)
            animateTotalPrice(
                start = 0L,
                end = basket.price.toLong(),
                textView = tvTotalPrice,
                view = view
            )
            //tvDebtPrice.text = getString(R.string.total_debt_text, basket.debt.toSumFormat)
            animateDebtPrice(
                start = 0L,
                end = basket.debt.toLong(),
                textView = tvDebtPrice,
                view = view
            )

            tvCardPrice.text = getString(R.string.sum_text, basket.card.toSumFormat)
            tvCashPrice.text = getString(R.string.sum_text, basket.cash.toSumFormat)

            etSearch.addTextChangedListener {
                adapter.models = orders.filter { product ->
                    product.productName.contains(it.toString(), true)
                }
            }
        }
    }
}
