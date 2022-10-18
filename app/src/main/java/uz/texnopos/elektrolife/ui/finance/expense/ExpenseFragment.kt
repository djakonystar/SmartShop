package uz.texnopos.elektrolife.ui.finance.expense

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.CalendarHelper
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.finance.Finance
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentExpenseBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.settings.Settings.Companion.FINANCE_EXPENSE
import uz.texnopos.elektrolife.ui.finance.FinanceDetailAdapter
import uz.texnopos.elektrolife.ui.finance.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFragment : Fragment(R.layout.fragment_expense) {
    private lateinit var binding: FragmentExpenseBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: FinanceViewModel by viewModel()
    private val adapter: FinanceDetailAdapter by inject()
    private val settings: Settings by inject()
    private val calendarHelper = CalendarHelper()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    private var dateFromInLong = calendarHelper.firstDayOfCurrentMonthMillis
    private var dateFrom = simpleDateFormat.format(dateFromInLong)
    private var dateToInLong = calendarHelper.currentDateMillis
    private var dateTo = simpleDateFormat.format(dateToInLong)
    private var lastSum = 0.0
    private var lastCashSum = 0.0
    private var lastCardSum = 0.0
    private var isLoading = false
    private var page = 1
    private var lastPage = 0
    private var expensesList = mutableListOf<Finance>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentExpenseBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.expenses)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            tvCashPrice.isSelected = true
            tvCardPrice.isSelected = true

            cardDate.onClick {
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

                    tvDateFrom.text = getString(R.string.date_from_text, dateFrom)
                    tvDateTo.text = getString(R.string.date_to_text, dateTo)

                    page = 1
                    expensesList.clear()
                    adapter.models = listOf()
                    viewModel.getFinanceDetails(
                        page = page,
                        from = dateFrom.changeDateFormat,
                        to = dateTo.changeDateFormat,
                        type = FINANCE_EXPENSE
                    )
                }

                dateRangePicker.addOnDismissListener {
                    cardDate.isEnabled = true
                }

                dateRangePicker.show(requireActivity().supportFragmentManager, dateRangePicker.tag)
                cardDate.isEnabled = false
            }

            tvDateFrom.text = getString(R.string.date_from_text, dateFrom)
            tvDateTo.text = getString(R.string.date_to_text, dateTo)

            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && btnFab.isVisible) btnFab.hide()
                    else if (dy < 0 && !btnFab.isVisible) btnFab.show()

                    if (!isLoading && page < lastPage && adapter.models.isNotEmpty() &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    ) {
                        page++
                        viewModel.getFinanceDetails(
                            page = page,
                            from = dateFrom.changeDateFormat,
                            to = dateTo.changeDateFormat,
                            type = FINANCE_EXPENSE
                        )
                    }
                }
            })

            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                page = 1
                lastPage = 0
                expensesList = mutableListOf()
                adapter.models = listOf()
                viewModel.getFinanceDetails(
                    page = page,
                    from = dateFrom.changeDateFormat,
                    to = dateTo.changeDateFormat,
                    type = FINANCE_EXPENSE
                )
            }

            btnFab.onClick {
                navController.navigate(R.id.action_expenseFragment_to_expenseAddFragment)
            }
        }

        viewModel.getFinanceDetails(
            page = page,
            from = dateFrom.changeDateFormat,
            to = dateTo.changeDateFormat,
            type = FINANCE_EXPENSE
        )
        setUpObservers()
    }

    override fun onDetach() {
        adapter.models = listOf()
        super.onDetach()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            topContainer.isEnabled = !loading
            swipeRefresh.isEnabled = !loading
            btnFab.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.financeDetails.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    val pageData = it.data!!
                    lastPage = pageData.lastPage
                    val pageExpenses = pageData.data.items

                    if (pageData.currentPage == 1) {
                        val newCash = pageData.data.amount.cash
                        val newCard = pageData.data.amount.card
                        val newSum = newCash + newCard
                        startAnimationCounter(lastSum, newSum)
                        animateCashSum(lastCashSum, newCash)
                        animateCardSum(lastCardSum, newCard)
                        lastSum = newSum
                        lastCashSum = newCash
                        lastCardSum = newCard
                    }

                    if (adapter.models.isEmpty()) {
                        adapter.models = pageExpenses
                        expensesList = pageExpenses as MutableList<Finance>
                    } else {
                        pageExpenses.forEach { expense ->
                            if (!expensesList.contains(expense)) expensesList.add(expense)
                        }
                        adapter.models = expensesList
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startAnimationCounter(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = (it.animatedValue as Float).toDouble()
                .format(2).toDouble().toSumFormat
            binding.tvTotalSum.text = context?.getString(
                R.string.price_text,
                newValue,
                settings.currency
            )
        }
        animator.duration = 300
        animator.start()
    }

    @SuppressLint("SetTextI18n")
    private fun animateCashSum(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = (it.animatedValue as Float).toDouble()
                .format(2).toDouble().toSumFormat
            binding.tvCashPrice.text = context?.getString(
                R.string.price_text,
                newValue,
                settings.currency
            )
        }
        animator.duration = 500
        animator.start()
    }

    @SuppressLint("SetTextI18n")
    private fun animateCardSum(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = (it.animatedValue as Float).toDouble()
                .format(2).toDouble().toSumFormat
            binding.tvCardPrice.text = context?.getString(
                R.string.price_text,
                newValue,
                settings.currency
            )
        }
        animator.duration = 500
        animator.start()
    }
}
