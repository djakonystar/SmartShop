package uz.texnopos.elektrolife.ui.sales

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.newsale.CatalogCategory
import uz.texnopos.elektrolife.data.model.sales.Sales
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
    private var typeOfPayment: Int = -1
    private val typesOfPayment = mutableSetOf<Int>()
    private var allSales = listOf<Sales>()
    private var dateFromInLong = System.currentTimeMillis()
    private var dateFrom = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateFromInLong)
    private var dateFromForBackend =
        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(dateFromInLong)
    private var dateToInLong = System.currentTimeMillis()
    private var dateTo = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateToInLong)
    private var dateToForBackend =
        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(dateToInLong)
    private var lastTotalPrice = 0L
    private var lastDebtPrice = 0L

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

                datePickerDialog.show(
                    requireActivity().supportFragmentManager,
                    datePickerDialog.tag
                )
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

                datePickerDialog.show(
                    requireActivity().supportFragmentManager,
                    datePickerDialog.tag
                )
            }

            btnCalculate.onClick {
                viewModel.getOrdersByDate(dateFromForBackend, dateToForBackend)
            }

//            chipGroup.setOnCheckedChangeListener { group, checkedId ->
//                typeOfPayment = checkedId % 3
//                Log.d("selectedIds", "CheckedChipIds: ${chipGroup.checkedChipIds.joinToString()}")
//                typesOfPayment.clear()
//                filterSales(group.checkedChipIds.map { it % 3 }.sorted())
//                Log.d("selectedIds", "CheckedChipId: $checkedId")
//                Log.d("selectedIds", "TypeOfPayment: $typeOfPayment")
//            }

            listOf(1, 2, 0).forEach {
                addNewChip(it)
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
        viewModel.orders.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        allSales = it.data.payload
                        filterSales()
                    } else {
                        showError(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    /**
     * Filtering sales by [id] only for single selection type
     */
    private fun filterSales(id: Int) {
        adapter.models = when (id) {
            1 -> allSales.filter { s -> s.basket.cash > 0 }
            2 -> allSales.filter { s -> s.basket.card > 0 }
            0 -> allSales.filter { s -> s.basket.debt > 0 }
            else -> allSales
        }
        val total = adapter.models.sumOf { sale -> sale.basket.price }.toLong()
        val debts = adapter.models.sumOf { sale -> sale.basket.debt }.toLong()
        animateTotalPrice(lastTotalPrice, total)
        animateDebtPrice(lastDebtPrice, debts)
        lastTotalPrice = total
        lastDebtPrice = debts
    }

    /**
     * Filtering sales by selected IDs only for multiple selection type
     */
    private fun filterSales() {
        val ids = typesOfPayment.sorted()
        Log.d("selectedIds", "IDs: ${ids.joinToString()}")
        adapter.models = when {
            ids.isEmpty() -> allSales
            ids.size == 3 -> {
                allSales.filter { s -> s.basket.cash > 0 && s.basket.card > 0 && s.basket.debt > 0 }
            }
            ids.size == 2 -> {
                if (ids[0] == 1 && ids[1] == 2) {
                    allSales.filter { s -> s.basket.cash > 0 && s.basket.card > 0 && s.basket.debt == 0.0 }
                } else if (ids[0] == 0 && ids[1] == 1) {
                    allSales.filter { s -> s.basket.cash > 0 && s.basket.debt > 0 && s.basket.card == 0.0 }
                } else {
                    allSales.filter { s -> s.basket.card > 0 && s.basket.debt > 0 && s.basket.cash == 0.0 }
                }
            }
            ids.size == 1 -> {
                when (ids[0]) {
                    1 -> allSales.filter { s -> s.basket.cash > 0 && s.basket.card == 0.0 && s.basket.debt == 0.0 }
                    2 -> allSales.filter { s -> s.basket.card > 0 && s.basket.cash == 0.0 && s.basket.debt == 0.0 }
                    else -> allSales.filter { s -> s.basket.debt > 0 && s.basket.cash == 0.0 && s.basket.card == 0.0 }
                }
            }
            else -> allSales
        }
        val total = adapter.models.sumOf { sale -> sale.basket.price }.toLong()
        val debts = adapter.models.sumOf { sale -> sale.basket.debt }.toLong()
        animateTotalPrice(lastTotalPrice, total)
        animateDebtPrice(lastDebtPrice, debts)
        lastTotalPrice = total
        lastDebtPrice = debts
    }

    private fun addNewChip(type: Int) {
        try {
            binding.apply {
                val inflater = LayoutInflater.from(requireContext())

                val newChip =
                    inflater.inflate(R.layout.item_chip_choice, chipGroup, false) as Chip
                newChip.text = when (type) {
                    1 -> context?.getString(R.string.payment_cash)
                    2 -> context?.getString(R.string.payment_card)
                    else -> context?.getString(R.string.payment_debt)
                }

                chipGroup.addView(newChip)

                newChip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        typesOfPayment.add(type)
                    } else {
                        typesOfPayment.remove(type)
                    }
                    filterSales()
                }
            }
        } catch (e: Exception) {
            showError(e.localizedMessage)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun animateTotalPrice(start: Long, end: Long) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = (it.animatedValue as Float).toLong().toSumFormat
            binding.tvTotalPrice.text = context?.getString(
                R.string.total_sum_text,
                newValue
            )
        }
        animator.duration = 500
        animator.start()
    }

    @SuppressLint("SetTextI18n")
    private fun animateDebtPrice(start: Long, end: Long) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = (it.animatedValue as Float).toLong().toSumFormat
            binding.tvDebtPrice.text = context?.getString(
                R.string.total_debt_text,
                newValue
            )
        }
        animator.duration = 500
        animator.start()
    }
}
