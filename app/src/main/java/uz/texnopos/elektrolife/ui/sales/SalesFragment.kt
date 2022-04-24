package uz.texnopos.elektrolife.ui.sales

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.Bundle
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentSalesBinding
import uz.texnopos.elektrolife.settings.Settings
import java.text.SimpleDateFormat
import java.util.*

class SalesFragment : Fragment(R.layout.fragment_sales) {
    private lateinit var binding: FragmentSalesBinding
    private lateinit var navController: NavController
    private val adapter: SalesAdapter by inject()
    private val viewModel: SalesViewModel by viewModel()
    private val settings: Settings by inject()
    private var typeOfPayment: Int = -1
    private val typesOfPayment = mutableSetOf<Int>()
    private var allSales = listOf<Basket>()
    private var dateFromInLong = System.currentTimeMillis()
    private var dateFrom = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateFromInLong)
    private var dateFromForBackend = dateFrom.changeDateFormat
    private var dateToInLong = System.currentTimeMillis()
    private var dateTo = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateToInLong)
    private var dateToForBackend = dateTo.changeDateFormat
    private var lastTotalPrice = 0.0
    private var lastDebtPrice = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSalesBinding.bind(view)
        navController = findNavController()

        binding.apply {
            tvTitle.text = context?.getString(R.string.sales)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter
            adapter.onClickItem {
                val json = GsonBuilder().setPrettyPrinting().create().toJson(it)
                navController.navigate(
                    SalesFragmentDirections.actionSalesFragmentToDetailSalesFragment(json)
                )
            }

            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                viewModel.getBaskets(dateFromForBackend, dateToForBackend)
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

        viewModel.getBaskets(dateFromForBackend, dateToForBackend)
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.baskets.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    allSales = it.data!!.data.baskets
                    filterSales()
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
            1 -> allSales.filter { s -> s.cash > 0 }
            2 -> allSales.filter { s -> s.card > 0 }
            0 -> allSales.filter { s -> s.debt.debt > 0 }
            else -> allSales
        }
        val total = adapter.models.sumOf { sale -> sale.cash } +
                adapter.models.sumOf { sale -> sale.card } +
                adapter.models.sumOf { sale -> sale.debt.debt }
        val debts = adapter.models.sumOf { sale -> sale.debt.debt }
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
        adapter.models = when {
            ids.isEmpty() -> allSales
            ids.size == 3 -> {
                allSales.filter { s -> s.cash > 0 && s.card > 0 && s.debt.debt > 0 }
            }
            ids.size == 2 -> {
                if (ids[0] == 1 && ids[1] == 2) {
                    allSales.filter { s -> s.cash > 0 && s.card > 0 && s.debt.debt == 0.0 }
                } else if (ids[0] == 0 && ids[1] == 1) {
                    allSales.filter { s -> s.cash > 0 && s.debt.debt > 0 && s.card == 0.0 }
                } else {
                    allSales.filter { s -> s.card > 0 && s.debt.debt > 0 && s.cash == 0.0 }
                }
            }
            ids.size == 1 -> {
                when (ids[0]) {
                    1 -> allSales.filter { s -> s.cash > 0 && s.card == 0.0 && s.debt.debt == 0.0 }
                    2 -> allSales.filter { s -> s.card > 0 && s.cash == 0.0 && s.debt.debt == 0.0 }
                    else -> allSales.filter { s -> s.debt.debt > 0 && s.cash == 0.0 && s.card == 0.0 }
                }
            }
            else -> allSales
        }
        val total = adapter.models.sumOf { sale -> sale.cash } +
                adapter.models.sumOf { sale -> sale.card } +
                adapter.models.sumOf { sale -> sale.debt.debt }
        val debts = adapter.models.sumOf { sale -> sale.debt.debt }
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
    private fun animateTotalPrice(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = (it.animatedValue as Float).toDouble()
            binding.tvTotalPrice.text = context?.getString(
                R.string.total_sum_text,
                newValue.checkModule.toSumFormat,
                settings.currency
            )
        }
        animator.duration = 500
        animator.start()
    }

    @SuppressLint("SetTextI18n")
    private fun animateDebtPrice(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = (it.animatedValue as Float).toDouble()
            binding.tvDebtPrice.text = context?.getString(
                R.string.total_debt_text,
                newValue.checkModule.toSumFormat,
                settings.currency
            )
        }
        animator.duration = 500
        animator.start()
    }
}
