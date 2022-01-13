package uz.texnopos.elektrolife.ui.sales

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentSalesBinding
import java.text.SimpleDateFormat
import java.util.*

class SalesFragment : Fragment(R.layout.fragment_sales) {
    private lateinit var binding: FragmentSalesBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val adapter: SalesAdapter by inject()
    private val viewModel: SalesViewModel by viewModel()
    private var dateFromInLong = System.currentTimeMillis()
    private var dateFrom = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateFromInLong)
    private var dateFromForBackend =
        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(dateFromInLong)
    private var dateToInLong = System.currentTimeMillis()
    private var dateTo = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateToInLong)
    private var dateToForBackend =
        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(dateToInLong)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSalesBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            adapter.onClickItem {
                val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                val gsonString = gsonPretty.toJson(it)
                navController.navigate(
                    SalesFragmentDirections.actionSalesFragmentToDetailSalesFragment(
                        gsonString
                    )
                )
            }

            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                viewModel.getOrdersByDate(dateFromForBackend, dateToForBackend)
            }

            etDateFrom.setText(dateFrom)
            etDateTo.setText(dateTo)

            etDateFrom.onClick {
                val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(context?.getString(R.string.start_date))
                    .setSelection(dateFromInLong)
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setEnd(System.currentTimeMillis())
                            .setValidator(DateValidatorPointBackward.now())
                            .build()
                    )
                    .build()

                datePickerDialog.addOnPositiveButtonClickListener {
                    dateFromInLong = it
                    dateFrom = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateFromInLong)
                    dateFromForBackend =
                        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(dateFromInLong)
                    etDateFrom.setText(dateFrom)
                }

                datePickerDialog.show(requireActivity().supportFragmentManager, "DatePicker")
            }

            etDateTo.onClick {
                val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(context?.getString(R.string.end_date))
                    .setSelection(dateToInLong)
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setEnd(System.currentTimeMillis())
                            .setValidator(DateValidatorPointBackward.now())
                            .build()
                    )
                    .build()

                datePickerDialog.addOnPositiveButtonClickListener {
                    dateToInLong = it
                    dateTo = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateToInLong)
                    dateToForBackend =
                        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(dateToInLong)
                    etDateTo.setText(dateTo)
                }

                datePickerDialog.show(requireActivity().supportFragmentManager, "DatePicker")
            }

            btnCalculate.onClick {
                viewModel.getOrdersByDate(dateFromForBackend, dateToForBackend)
            }
        }


        viewModel.getOrdersByDate(dateFromForBackend, dateToForBackend)
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            tilDateFrom.isEnabled = !loading
            tilDateTo.isEnabled = !loading
            btnCalculate.isEnabled = !loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.orders.observe(viewLifecycleOwner, {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        adapter.models = it.data.payload
                        val total = it.data.payload.sumOf { sale -> sale.basket.price }.toLong()
                        val debts = it.data.payload.sumOf { sale -> sale.basket.debt }.toLong()
                        binding.tvTotalPrice.text =
                            context?.getString(R.string.total_sum_text, total.toSumFormat)
                        binding.tvDebtPrice.text =
                            context?.getString(R.string.total_debt_text, debts.toSumFormat)
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        })
    }
}
