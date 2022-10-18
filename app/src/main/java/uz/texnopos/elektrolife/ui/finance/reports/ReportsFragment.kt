package uz.texnopos.elektrolife.ui.finance.reports

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
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentReportsBinding
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment(R.layout.fragment_reports) {
    private lateinit var binding: FragmentReportsBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: ReportsViewModel by viewModel()
    private val calendarHelper = CalendarHelper()
    private var lastSumOfCashboxCash = 0.0
    private var lastSumOfCashboxCard = 0.0
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

        binding = FragmentReportsBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = getString(R.string.reports)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            tvCashboxCash.isSelected = true
            tvCashboxCard.isSelected = true
            ivProfitMoney.isSelected = true

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                viewModel.getCashbox(
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

                    viewModel.getCashbox(
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

            tvRangeCashbox.text =
                context?.getString(R.string.date_range_text, cashboxDateFrom, cashboxDateTo)
            tvRangeProfit.text =
                context?.getString(R.string.date_range_text, profitDateFrom, profitDateTo)
        }

        viewModel.getCashbox(cashboxDateFrom.changeDateFormat, cashboxDateTo.changeDateFormat)
        viewModel.getProfit(profitDateFrom.changeDateFormat, profitDateTo.changeDateFormat)
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            btnCashboxDate.isEnabled = !loading
            btnProfitDate.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.cashbox.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    val newCashSum = it.data!!.payload.cash
                    val newCardSum = it.data.payload.card
                    animateCashboxCash(lastSumOfCashboxCash, newCashSum)
                    animateCashboxCard(lastSumOfCashboxCard, newCardSum)
                    lastSumOfCashboxCash = newCashSum
                    lastSumOfCashboxCard = newCardSum
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }

        viewModel.profit.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    val newSum = it.data!!.payload.profit!!
                    animateProfitBalance(lastSumOfProfit, newSum)
                    lastSumOfProfit = newSum
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun animateCashboxCash(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = "%.2f".format((it.animatedValue as Float).toDouble())
                .replace(',', '.').toDouble().toSumFormat
            binding.tvCashboxCash.text = context?.getString(
                R.string.sum_text,
                newValue
            )
        }
        animator.duration = 300
        animator.start()
    }

    @SuppressLint("SetTextI18n")
    private fun animateCashboxCard(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = "%.2f".format((it.animatedValue as Float).toDouble())
                .replace(',', '.').toDouble().toSumFormat
            binding.tvCashboxCard.text = context?.getString(
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
