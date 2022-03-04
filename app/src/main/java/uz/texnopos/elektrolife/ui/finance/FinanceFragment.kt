package uz.texnopos.elektrolife.ui.finance

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.CalendarHelper
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showMessage
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentFinanceBinding
import java.text.SimpleDateFormat
import java.util.*

class FinanceFragment : Fragment(R.layout.fragment_finance) {
    private lateinit var binding: FragmentFinanceBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: FinanceViewModel by viewModel()
    private val calendarHelper = CalendarHelper()
    private var lastSumOfCashbox = 0.0
    private var lastSumOfProfit = 0.0
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    private var cashboxDateFromInLong = calendarHelper.firstDayOfCurrentMonthMillis
    private var cashboxDateFrom = calendarHelper.firstDayOfCurrentMonth
    private var cashboxDateToInLong = calendarHelper.currentDateMillis
    private var cashboxDateTo = calendarHelper.currentDate
    private var profitDateFromInLong = calendarHelper.firstDayOfCurrentMonthMillis
    private var profitDateFrom = calendarHelper.firstDayOfCurrentMonth
    private var profitDateToInLong = calendarHelper.currentDateMillis
    private var profitDateTo = calendarHelper.currentDate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFinanceBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.finance)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                viewModel.getCashboxBalance(
                    cashboxDateFrom.changeDateFormat,
                    cashboxDateTo.changeDateFormat
                )
                viewModel.getProfit(profitDateFrom.changeDateFormat, profitDateTo.changeDateFormat)
            }

            btnCashboxDate.onClick {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setSelection(
                        Pair(
                            calendarHelper.firstDayOfCurrentMonthMillis,
                            calendarHelper.currentDateMillis
                        )
                    )
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointBackward.before(calendarHelper.currentDateMillis))
                            .build()
                    )
                    .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                    .setTitleText(R.string.choose_range)
                    .build()

                dateRangePicker.addOnPositiveButtonClickListener { dates: Pair<Long, Long> ->
                    cashboxDateFromInLong = dates.first
                    cashboxDateFrom = simpleDateFormat.format(cashboxDateFromInLong)
                    cashboxDateToInLong = dates.second
                    cashboxDateTo = simpleDateFormat.format(cashboxDateToInLong)

                    tvRangeCashbox.text =
                        context?.getString(R.string.date_range_text, cashboxDateFrom, cashboxDateTo)

                    viewModel.getCashboxBalance(
                        cashboxDateFrom.changeDateFormat,
                        cashboxDateTo.changeDateFormat
                    )
                }

                dateRangePicker.addOnDismissListener {
                    btnCashboxDate.isEnabled = true
                }

                dateRangePicker.show(requireActivity().supportFragmentManager, dateRangePicker.tag)
                btnCashboxDate.isEnabled = false
            }

            btnProfitDate.onClick {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setSelection(
                        Pair(
                            calendarHelper.firstDayOfCurrentMonthMillis,
                            calendarHelper.currentDateMillis
                        )
                    )
                    .setCalendarConstraints(
                        CalendarConstraints.Builder()
                            .setValidator(DateValidatorPointBackward.before(calendarHelper.currentDateMillis))
                            .build()
                    )
                    .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                    .setTitleText(R.string.choose_range)
                    .build()

                dateRangePicker.addOnPositiveButtonClickListener { dates: Pair<Long, Long> ->
                    profitDateFromInLong = dates.first
                    profitDateFrom = simpleDateFormat.format(profitDateFromInLong)
                    profitDateToInLong = dates.second
                    profitDateTo = simpleDateFormat.format(profitDateToInLong)

                    tvRangeProfit.text =
                        context?.getString(R.string.date_range_text, profitDateFrom, profitDateTo)

                    viewModel.getProfit(
                        profitDateFrom.changeDateFormat,
                        profitDateTo.changeDateFormat
                    )
                }

                dateRangePicker.addOnDismissListener {
                    btnProfitDate.isEnabled = true
                }

                dateRangePicker.show(requireActivity().supportFragmentManager, dateRangePicker.tag)
                btnProfitDate.isEnabled = false
            }

            cardSales.onClick {
                navController.navigate(R.id.action_financeFragment_to_salesFragment)
            }
            cardIncomes.onClick {
                navController.navigate(R.id.action_financeFragment_to_incomeFragment)
            }
            cardExpenses.onClick {
                navController.navigate(R.id.action_financeFragment_to_expenseFragment)
            }

            tvRangeCashbox.text =
                context?.getString(R.string.date_range_text, cashboxDateFrom, cashboxDateTo)
            tvRangeProfit.text =
                context?.getString(R.string.date_range_text, profitDateFrom, profitDateTo)
        }

        viewModel.getCashboxBalance(
            cashboxDateFrom.changeDateFormat,
            cashboxDateTo.changeDateFormat
        )
        viewModel.getProfit(profitDateFrom.changeDateFormat, profitDateTo.changeDateFormat)
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.cashboxBalance.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        val newSum = it.data.payload.balance
                        animateCashboxBalance(lastSumOfCashbox, newSum)
                        lastSumOfCashbox = newSum
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }

        viewModel.profit.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        val newSum = it.data.payload.balance
                        animateProfitBalance(lastSumOfProfit, newSum)
                        lastSumOfProfit = newSum
                    } else {
                        showMessage(it.data.message)
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showMessage(it.message)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun animateCashboxBalance(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = "%.2f".format((it.animatedValue as Float).toDouble())
                .replace(',', '.').toDouble().toSumFormat
            binding.tvCashboxMoney.text = context?.getString(
                R.string.sum_text,
                newValue
            )
        }
        animator.duration = 300
        animator.start()
    }

    @SuppressLint("SetTextI18n")
    private fun animateProfitBalance(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = "%.2f".format((it.animatedValue as Float).toDouble())
                .replace(',', '.').toDouble().toSumFormat
            binding.tvProfitMoney.text = context?.getString(
                R.string.sum_text,
                newValue
            )
        }
        animator.duration = 300
        animator.start()
    }
}
