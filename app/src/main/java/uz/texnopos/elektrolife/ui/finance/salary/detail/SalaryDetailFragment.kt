package uz.texnopos.elektrolife.ui.finance.salary.detail

import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.CalendarHelper
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.databinding.FragmentSalaryDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class SalaryDetailFragment : Fragment(R.layout.fragment_salary_detail) {
    private lateinit var binding: FragmentSalaryDetailBinding
    private lateinit var navController: NavController
    private val viewModel: SalaryDetailViewModel by viewModel()
    private val adapter: SalaryDetailAdapter by inject()
    private val args: SalaryDetailFragmentArgs by navArgs()
    private val calendarHelper = CalendarHelper()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    private var salaryDateFromInLong = calendarHelper.firstDayOfCurrentMonthMillis
    private var salaryDateFrom = calendarHelper.firstDayOfCurrentMonth
    private var salaryDateToInLong = calendarHelper.currentDateMillis
    private var salaryDateTo = calendarHelper.currentDate
    private var employeeId: Int = -1
    private var employeeName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSalaryDetailBinding.bind(view)
        navController = findNavController()
        employeeId = args.employeeId
        employeeName = args.employeeName

        binding.apply {
            if (employeeName == "null") {
                tvTitle.text = getString(R.string.salaries)
            } else {
                tvTitle.text = employeeName
            }

            btnHome.onClick {
                navController.popBackStack()
            }

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                viewModel.getMonthlySalary(
                    employeeId,
                    salaryDateFrom.changeDateFormat,
                    salaryDateTo.changeDateFormat
                )
            }

            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                )
            )
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && fabDatePicker.isVisible) fabDatePicker.hide()
                    else if (dy < 0 && !fabDatePicker.isVisible) fabDatePicker.show()
                }
            })

            fabDatePicker.onClick {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setSelection(
                        Pair(salaryDateFromInLong, salaryDateToInLong)
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
                    salaryDateFromInLong = dates.first
                    salaryDateFrom = simpleDateFormat.format(salaryDateFromInLong)
                    salaryDateToInLong = dates.second
                    salaryDateTo = simpleDateFormat.format(salaryDateToInLong)

                    viewModel.getMonthlySalary(
                        employeeId,
                        salaryDateFrom.changeDateFormat,
                        salaryDateTo.changeDateFormat
                    )
                }

                dateRangePicker.addOnDismissListener {
                    fabDatePicker.isEnabled = true
                }

                dateRangePicker.show(requireActivity().supportFragmentManager, dateRangePicker.tag)
                fabDatePicker.isEnabled = false
            }
        }

        viewModel.getMonthlySalary(
            employeeId,
            salaryDateFrom.changeDateFormat,
            salaryDateTo.changeDateFormat
        )
        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
            fabDatePicker.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.monthlySalary.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    adapter.models = it.data!!
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        adapter.models = listOf()
    }
}
