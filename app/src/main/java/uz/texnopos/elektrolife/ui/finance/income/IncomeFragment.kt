package uz.texnopos.elektrolife.ui.finance.income

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.databinding.ActionBarBinding
import uz.texnopos.elektrolife.databinding.FragmentExpenseBinding
import uz.texnopos.elektrolife.settings.Settings.Companion.FINANCE_INCOME
import uz.texnopos.elektrolife.ui.finance.FinanceDetailAdapter
import uz.texnopos.elektrolife.ui.finance.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

class IncomeFragment : Fragment(R.layout.fragment_income) {
    private lateinit var binding: FragmentExpenseBinding
    private lateinit var abBinding: ActionBarBinding
    private lateinit var navController: NavController
    private val viewModel: FinanceViewModel by viewModel()
    private val adapter: FinanceDetailAdapter by inject()
    private var dateFromInLong = System.currentTimeMillis()
    private var dateFrom = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateFromInLong)
    private var dateToInLong = System.currentTimeMillis()
    private var dateTo = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(dateToInLong)
    private var lastSum = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentExpenseBinding.bind(view)
        abBinding = ActionBarBinding.bind(view)
        navController = findNavController()

        abBinding.apply {
            tvTitle.text = context?.getString(R.string.incomes)
            btnHome.onClick {
                navController.popBackStack()
            }
        }

        binding.apply {
            recyclerView.adapter = adapter

            swipeRefresh.setOnRefreshListener {
                setLoading(false)
                swipeRefresh.isRefreshing = false
                viewModel.getFinanceDetails(
                    from = dateFrom.changeDateFormat,
                    to = dateTo.changeDateFormat,
                    type = FINANCE_INCOME
                )
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
                    etDateTo.setText(dateTo)
                }

                datePickerDialog.show(requireActivity().supportFragmentManager, "DatePicker")
            }

            btnCalculate.onClick {
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
                    if (it.data!!.successful) {
                        adapter.models = it.data.payload
                        val newSum = it.data.payload.sumOf { f -> f.price }
                        startAnimationCounter(lastSum, newSum)
                        lastSum = newSum
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

    @SuppressLint("SetTextI18n")
    private fun startAnimationCounter(start: Double, end: Double) {
        val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
        animator.addUpdateListener {
            val newValue = "%.2f".format((it.animatedValue as Float).toDouble())
                .replace(',', '.').toDouble().toSumFormat
            binding.tvTotalSum.text = context?.getString(
                R.string.sum_text,
                newValue
            )
        }
        animator.duration = 500
        animator.start()
    }
}
