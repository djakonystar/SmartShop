package uz.texnopos.elektrolife.ui.sales

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.GsonBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.CalendarHelper
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.data.model.sales.BasketResponse
import uz.texnopos.elektrolife.databinding.FragmentSalesBinding
import uz.texnopos.elektrolife.settings.Settings
import java.text.SimpleDateFormat
import java.util.*

class SalesFragment : Fragment(R.layout.fragment_sales) {
    private lateinit var binding: FragmentSalesBinding
    private lateinit var navController: NavController

    private val viewModel: SalesViewModel by viewModel()
    private val adapter: SalesAdapter by inject()
    private val settings: Settings by inject()

    private val typesOfPayment = mutableSetOf<Int>()
    private var allSales = mutableListOf<Basket>()
    private lateinit var baskets: BasketResponse

    private val calendarHelper = CalendarHelper()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    private var dateFromInLong = calendarHelper.firstDayOfCurrentMonthMillis
    private var dateFrom = calendarHelper.firstDayOfCurrentMonth
    private var dateToInLong = calendarHelper.currentDateMillis
    private var dateTo = calendarHelper.currentDate

    private var isLoading = false
    private var page = 1
    private var lastPage = 0

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
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && fabDatePicker.isVisible) fabDatePicker.hide()
                    else if (dy < 0 && !fabDatePicker.isVisible) fabDatePicker.show()

                    if (!isLoading && adapter.models.isNotEmpty() && page < lastPage &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    ) {
                        page++
                        viewModel.getBaskets(dateFrom.changeDateFormat, dateTo.changeDateFormat, page)
                    }
                }
            })

            adapter.onClickItem {
                val json = GsonBuilder().setPrettyPrinting().create().toJson(it)
                navController.navigate(
                    SalesFragmentDirections.actionSalesFragmentToDetailSalesFragment(json)
                )
            }

            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                if (typesOfPayment.isEmpty()) {
                    page = 1
                    allSales = mutableListOf()
                    viewModel.getBaskets(dateFrom.changeDateFormat, dateTo.changeDateFormat, page)
                } else {
                    filterSales()
                }
            }

            listOf(1, 2, 0).forEach {
                addNewChip(it)
            }

            fabDatePicker.onClick {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setSelection(
                        androidx.core.util.Pair(dateFromInLong, dateToInLong)
                    )
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointBackward.before(calendarHelper.currentDateMillis))
                            .build()
                    )
                    .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                    .setTitleText(R.string.choose_range)
                    .build()

                dateRangePicker.addOnPositiveButtonClickListener { dates ->
                    dateFromInLong = dates.first
                    dateFrom = simpleDateFormat.format(dateFromInLong)
                    dateToInLong = dates.second
                    dateTo = simpleDateFormat.format(dateToInLong)

                    page = 1
                    allSales = mutableListOf()
                    viewModel.getBaskets(
                        dateFrom.changeDateFormat,
                        dateTo.changeDateFormat,
                        page
                    )
                }

                dateRangePicker.addOnDismissListener {
                    fabDatePicker.isEnabled = true
                }

                dateRangePicker.show(requireActivity().supportFragmentManager, dateRangePicker.tag)
                fabDatePicker.isEnabled = false
            }
        }


        page = 1
        lastPage = 0
        viewModel.getBaskets(dateFrom.changeDateFormat, dateTo.changeDateFormat, page)
        setUpObservers()
    }

    override fun onDetach() {
        super.onDetach()
        adapter.models = listOf()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
            fabDatePicker.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.baskets.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if(page == 1){
                        allSales.clear()
                    }
                    it.data!!.data.baskets.forEach {basket ->
                        allSales.add(basket)
                    }
                    adapter.models = allSales
                    baskets = it.data.data

                    lastPage = it.data.lastPage
                    setAmount()
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    /**
     * Filtering sales by selected IDs only for multiple selection type
     */
    private fun filterSales() {
        val ids = typesOfPayment.sorted()
        val typesOfPayment: String = when {
            ids.isEmpty() -> ""
            ids.size == 3 -> {
                "cash|card|debt"
            }
            ids.size == 2 -> {
                if (ids[0] == 1 && ids[1] == 2) {
                    "cash|card"
                } else if (ids[0] == 0 && ids[1] == 1) {
                    "cash|debt"
                } else {
                    "card|debt"
                }
            }
            ids.size == 1 -> {
                when (ids[0]) {
                    1 -> "cash"
                    2 -> "card"
                    else -> "debt"
                }
            }
            else -> ""
        }
        allSales = mutableListOf()
        viewModel.filterBaskets(typesOfPayment, dateFrom.changeDateFormat, dateTo.changeDateFormat)
    }

    private fun setAmount() {
        val amount = baskets.amount
        binding.apply {
            tvTotalPrice.text = getString(
                R.string.total_price_text,
                amount.sum.toSumFormat,
                settings.currency
            )
            tvCashPrice.text = getString(
                R.string.cash_price_text,
                amount.cash.toSumFormat,
                settings.currency
            )
            tvCardPrice.text = getString(
                R.string.card_price_text,
                amount.card.toSumFormat,
                settings.currency
            )
            tvDebtPrice.text = getString(
                R.string.debt_price_text,
                amount.debt.toSumFormat,
                settings.currency
            )
            tvDebtPaidPrice.text = getString(
                R.string.debt_paid_price_text,
                amount.paidDebt.toSumFormat,
                settings.currency
            )
            tvDebtRemainedPrice.text = getString(
                R.string.debt_remained_price_text,
                amount.remaining.toSumFormat,
                settings.currency
            )
        }
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
}
