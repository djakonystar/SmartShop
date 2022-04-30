package uz.texnopos.elektrolife.ui.finance.income

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
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
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentIncomeBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.settings.Settings.Companion.FINANCE_INCOME
import uz.texnopos.elektrolife.ui.finance.FinanceDetailAdapter
import uz.texnopos.elektrolife.ui.finance.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

class IncomeFragment : Fragment(R.layout.fragment_income) {
    private lateinit var binding: FragmentIncomeBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentIncomeBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.incomes)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
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

                    viewModel.getFinanceDetails(
                        from = dateFrom.changeDateFormat,
                        to = dateTo.changeDateFormat,
                        type = FINANCE_INCOME
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

            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && btnFab.isVisible) btnFab.hide()
                    else if (dy < 0 && !btnFab.isVisible) btnFab.show()
                }
            })

            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                viewModel.getFinanceDetails(
                    from = dateFrom.changeDateFormat,
                    to = dateTo.changeDateFormat,
                    type = FINANCE_INCOME
                )
            }

            btnFab.onClick {
                navController.navigate(R.id.action_incomeFragment_to_incomeAddFragment)
            }
        }

        viewModel.getFinanceDetails(
            from = dateFrom.changeDateFormat,
            to = dateTo.changeDateFormat,
            type = FINANCE_INCOME
        )
        setUpObservers()
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
                        adapter.models = it.data!!
                        val newSum = it.data.sumOf { f -> f.price }
                        startAnimationCounter(lastSum, newSum)
                        lastSum = newSum
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
        animator.duration = 500
        animator.start()
    }
}
