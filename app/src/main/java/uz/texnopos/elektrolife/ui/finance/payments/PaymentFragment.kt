package uz.texnopos.elektrolife.ui.finance.payments

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
import uz.texnopos.elektrolife.core.extensions.changeDateFormat
import uz.texnopos.elektrolife.core.extensions.onClick
import uz.texnopos.elektrolife.core.extensions.showError
import uz.texnopos.elektrolife.core.extensions.toSumFormat
import uz.texnopos.elektrolife.data.model.payment.Amount
import uz.texnopos.elektrolife.data.model.payment.Payment
import uz.texnopos.elektrolife.databinding.FragmentPaymentBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.client.detail.payment.ClientPaymentAdapter
import uz.texnopos.elektrolife.ui.payment.PaymentViewModel
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment(R.layout.fragment_payment) {
    private lateinit var binding: FragmentPaymentBinding
    private lateinit var navController: NavController
    private val viewModel: PaymentViewModel by viewModel()
    private val adapter: ClientPaymentAdapter by inject()
    private val settings: Settings by inject()

    private var paymentsList = mutableListOf<Payment>()
    private lateinit var amount: Amount
    private var isLoading = false
    private var page = 1
    private var lastPage = 0

    // Working with date picker
    private val calendarHelper = CalendarHelper()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    private var dateFromInLong = calendarHelper.firstDayOfCurrentMonthMillis
    private var dateFrom = calendarHelper.firstDayOfCurrentMonth
    private var dateToInLong = calendarHelper.currentDateMillis
    private var dateTo = calendarHelper.currentDate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentBinding.bind(view)
        navController = findNavController()

        binding.apply {
            btnHome.onClick {
                navController.popBackStack()
            }

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setLoading(false)
                paymentsList = mutableListOf()
                adapter.models = listOf()
                page = 1
                viewModel.getPayments(
                    page = page,
                    from = dateFrom.changeDateFormat,
                    to = dateTo.changeDateFormat
                )
            }

            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && fabDatePicker.isVisible) fabDatePicker.hide()
                    else if (dy < 0 && !fabDatePicker.isVisible) fabDatePicker.show()

                    if (!isLoading && adapter.models.isNotEmpty() && page < lastPage &&
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    ) {
                        page++
                        viewModel.getPayments(
                            page = page,
                            from = dateFrom.changeDateFormat,
                            to = dateTo.changeDateFormat
                        )
                    }
                }
            })

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
                    paymentsList = mutableListOf()
                    adapter.models = listOf()

                    viewModel.getPayments(
                        page = page,
                        from = dateFrom.changeDateFormat,
                        to = dateTo.changeDateFormat
                    )
                }

                dateRangePicker.addOnDismissListener {
                    fabDatePicker.isEnabled = true
                }

                dateRangePicker.show(requireActivity().supportFragmentManager, dateRangePicker.tag)
                fabDatePicker.isEnabled = false
            }
        }

        viewModel.getPayments(page, dateFrom.changeDateFormat, dateTo.changeDateFormat)
        setUpObservers()
    }

    override fun onDetach() {
        adapter.models = listOf()
        super.onDetach()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            swipeRefresh.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.payments.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    lastPage = it.data!!.lastPage
                    if (it.data.currentPage == 1) amount = it.data.data.amount
                    setAmount()
                    val allPaymentsList = it.data.data.histories
                    if (adapter.models.isEmpty()) {
                        adapter.models = allPaymentsList
                        paymentsList = allPaymentsList as MutableList<Payment>
                    } else {
                        it.data.data.histories.forEach { payment ->
                            if (!paymentsList.contains(payment)) {
                                paymentsList.add(payment)
                            }
                        }
                        adapter.models = paymentsList
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    showError(it.message)
                }
            }
        }
    }

    private fun setAmount() {
        binding.apply {
            tvTotalPrice.text = getString(
                R.string.total_price_text,
                (amount.cash + amount.card).toSumFormat,
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
        }
    }
}