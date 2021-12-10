package uz.texnopos.electrolightwarehouse.ui.sales

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.core.ResourceState
import uz.texnopos.electrolightwarehouse.core.extensions.onClick
import uz.texnopos.electrolightwarehouse.databinding.ActionBarBinding
import uz.texnopos.electrolightwarehouse.databinding.FragmentSalesBinding
import java.util.*

class SalesFragment : Fragment(R.layout.fragment_sales) {
    private lateinit var binding: FragmentSalesBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val adapter: SalesAdapter by inject()
    private val viewModel: SalesViewModel by viewModel()
    private lateinit var setListener: DatePickerDialog.OnDateSetListener
    private val plusDay = 86399999
    private var from = ""
    private var to = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getOrders()
        binding = FragmentSalesBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        binding.apply {
            etFrom.onClick {
                val calendar = Calendar.getInstance()
                val year = calendar.get((Calendar.YEAR))
                var month = calendar.get((Calendar.MONTH))
                val day = calendar.get((Calendar.DAY_OF_MONTH))

                setListener = DatePickerDialog.OnDateSetListener { _, year1, month1, dayOfMonth1 ->
                    month = month1+1
                    from = "$year1-$month-$dayOfMonth1"
                    etFrom.setText(from)
                }
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setListener
                    ,year, month, day)

                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                datePickerDialog.show()
            }

            etTo.onClick {
                val calendar = Calendar.getInstance()
                val year = calendar.get((Calendar.YEAR))
                var month = calendar.get((Calendar.MONTH))
                val day = calendar.get((Calendar.DAY_OF_MONTH))

                setListener = DatePickerDialog.OnDateSetListener { _, year1, month1, dayOfMonth1 ->
                    month = month1+1
                    to = "$year1-$month-$dayOfMonth1"
                    etTo.setText(to)
                }
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setListener
                    ,year, month, day)

                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                datePickerDialog.show()

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { dialog, _ ->
                    datePickerDialog.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                    if (from.isNotEmpty()&& to.isNotEmpty()){
                        viewModel.getOrdersByDate(from, to)
                    }else{
                        Toast.makeText(requireContext(), "sana kiriting", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            swipeRefresh.setOnRefreshListener {
                viewModel.getOrders()
            }
        }

        adapter.onClickItem {
            val gsonPretty= GsonBuilder().setPrettyPrinting().create()
            val gsonString=gsonPretty.toJson(it)
            navController.navigate(SalesFragmentDirections.actionSalesFragmentToDetailSalesFragment(gsonString))
        }

        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.orders.observe(viewLifecycleOwner, {
            when (it.status) {
                ResourceState.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                ResourceState.SUCCESS -> {
                    adapter.models = it.data!!
                    var price = 0.0
                    for (element in adapter.models){
                        price +=  element.basket.price
                    }
                    binding.tvTotalPrice.text = "Summa : "+(price.changeFormat())
                    binding.swipeRefresh.isRefreshing = false
                }
                ResourceState.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        })
    }
    private fun Double.changeFormat(): String {
        val num = this.toLong().toString()
        var s = ""
        val sz = num.length
        for (i in 0 until sz) {
            if (i != 0 && (i - sz % 3) % 3 == 0) s += ' '
            s += num[i]
        }
        return "$s uzs"
    }
}